package com.dine.DINERestaurant_Backend.reservation.service;

import com.dine.DINERestaurant_Backend.reservation.entity.Reservation;
import com.dine.DINERestaurant_Backend.reservation.entity.RestaurantTable;
import com.dine.DINERestaurant_Backend.reservation.repository.ReservationRepository;
import com.dine.DINERestaurant_Backend.reservation.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    // Kiểm tra bàn trống
    public List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, Integer guestCount) {

        // Lấy các bàn đủ chỗ + còn TRỐNG
        List<RestaurantTable> suitableTables =
                tableRepository.findByCapacityGreaterThanEqualAndStatus(guestCount, "Trống");

        List<RestaurantTable> availableTables = new ArrayList<>();

        for (RestaurantTable table : suitableTables) {
            if (isTableAvailable(table.getTableId(), date, time)) {
                availableTables.add(table);
            }
        }

        return availableTables;
    }


    private boolean isTableAvailable(Integer tableId, LocalDate date, LocalTime time) {
        List<Reservation> reservations = reservationRepository.findByTableAndDate(tableId, date);

        for (Reservation res : reservations) {
            LocalTime resStart = res.getReservationTime();
            LocalTime resEnd = resStart.plusHours(2); // mỗi đặt bàn 2 tiếng

            if (!time.isBefore(resStart) && time.isBefore(resEnd)) {
                return false;
            }
        }
        return true;
    }

    // TẠO ĐẶT BÀN MỚI – ĐÃ FIX LỖI ID NULL
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        Integer tableId = reservation.getTableId();

        // FIX CHÍNH TẠI ĐÂY: Kiểm tra tableId không null + tồn tại
        if (tableId == null) {
            throw new IllegalArgumentException("tableId là bắt buộc!");
        }

        RestaurantTable table = tableRepository.findById(reservation.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));

        // Kiểm tra bàn có trống không
        if (!isTableAvailable(tableId, reservation.getReservationDate(), reservation.getReservationTime())) {
            throw new RuntimeException("Bàn này đã được đặt vào thời gian bạn chọn!");
        }

        // Cập nhật trạng thái bàn
        table.setStatus("Đã đặt");
        tableRepository.save(table);

        // Thiết lập thông tin đặt bàn
        reservation.setQrCode(generateQRCode());
        reservation.setStatus("Chờ xác nhận");
        reservation.setCreatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    // Xem đặt bàn của user
    public List<Reservation> getUserReservations(Integer userId) {
        return reservationRepository.findUserReservationsDesc(userId);
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // CẬP NHẬT ĐẶT BÀN
    @Transactional
    public Reservation updateReservation(Integer id, Reservation updatedReservation) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đặt bàn với ID: " + id));

        Integer newTableId = updatedReservation.getTableId();
        if (newTableId == null) {
            throw new IllegalArgumentException("tableId không được để trống khi cập nhật!");
        }

        // Kiểm tra bàn mới có trống không (nếu đổi bàn hoặc thời gian)
        boolean needCheck = !existing.getTableId().equals(newTableId) ||
                !existing.getReservationDate().equals(updatedReservation.getReservationDate()) ||
                !existing.getReservationTime().equals(updatedReservation.getReservationTime());

        if (needCheck && !isTableAvailable(newTableId,
                updatedReservation.getReservationDate(),
                updatedReservation.getReservationTime())) {
            throw new RuntimeException("Bàn mới không trống vào thời gian bạn chọn!");
        }

        // Nếu đổi bàn → trả bàn cũ về trạng thái trống
        if (!existing.getTableId().equals(newTableId)) {
            tableRepository.findById(existing.getTableId())
                    .ifPresent(t -> {
                        t.setStatus("Trống");
                        tableRepository.save(t);
                    });
            // Cập nhật bàn mới
            tableRepository.findById(newTableId)
                    .ifPresent(t -> {
                        t.setStatus("Đã đặt");
                        tableRepository.save(t);
                    });
        }

        existing.setTableId(newTableId);
        existing.setReservationDate(updatedReservation.getReservationDate());
        existing.setReservationTime(updatedReservation.getReservationTime());
        existing.setGuestCount(updatedReservation.getGuestCount());
        existing.setNote(updatedReservation.getNote());

        return reservationRepository.save(existing);
    }

    // HỦY ĐẶT BÀN
    @Transactional
    public boolean cancelReservation(Integer id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đặt bàn!"));

        res.setStatus("Đã hủy");

        tableRepository.findById(res.getTableId())
                .ifPresent(t -> {
                    t.setStatus("Trống");
                    tableRepository.save(t);
                });

        reservationRepository.save(res);
        return true;
    }

    // Check-in QR
    @Transactional
    public Reservation checkInWithQR(String qrCode) {
        Reservation res = reservationRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("QR không hợp lệ!"));

        if ("Đã xác nhận".equals(res.getStatus())) {
            res.setStatus("Hoàn thành");

            tableRepository.findById(res.getTableId())
                    .ifPresent(t -> {
                        t.setStatus("Đang sử dụng");
                        tableRepository.save(t);
                    });

            return reservationRepository.save(res);
        }
        throw new RuntimeException("Đơn đặt bàn chưa được xác nhận!");
    }

    private String generateQRCode() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}