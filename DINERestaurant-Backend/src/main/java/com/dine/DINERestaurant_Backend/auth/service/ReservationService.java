package com.dine.DINERestaurant_Backend.auth.service;
import com.dine.DINERestaurant_Backend.auth.entity.Reservation;
import com.dine.DINERestaurant_Backend.auth.entity.RestaurantTable;
import com.dine.DINERestaurant_Backend.auth.repository.ReservationRepository;
import com.dine.DINERestaurant_Backend.auth.repository.RestaurantTableRepository;
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

    /**
     * Kiểm tra bàn trống
     */
    public List<RestaurantTable> getAvailableTables(LocalDate date, LocalTime time, Integer guestCount) {
        // Lấy tất cả bàn đủ sức chứa
        List<RestaurantTable> suitableTables = tableRepository.findByCapacityGreaterThanEqual(guestCount);

        List<RestaurantTable> availableTables = new ArrayList<>();

        for (RestaurantTable table : suitableTables) {
            if (isTableAvailable(table.getTableId(), date, time)) {
                availableTables.add(table);
            }
        }

        return availableTables;
    }

    /**
     * Kiểm tra bàn có trống không
     */
    private boolean isTableAvailable(Integer tableId, LocalDate date, LocalTime time) {
        List<Reservation> reservations = reservationRepository.findByTableAndDate(tableId, date);

        for (Reservation res : reservations) {
            // Kiểm tra khoảng thời gian (giả sử mỗi đặt bàn kéo dài 2 giờ)
            LocalTime resStart = res.getReservationTime();
            LocalTime resEnd = resStart.plusHours(2);

            if (time.isAfter(resStart) && time.isBefore(resEnd)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tạo đặt bàn mới
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Kiểm tra bàn có trống không
        if (!isTableAvailable(reservation.getTableId(),
                reservation.getReservationDate(),
                reservation.getReservationTime())) {
            throw new RuntimeException("Table is not available at this time");
        }

        // Tạo mã QR
        reservation.setQrCode(generateQRCode());
        reservation.setStatus("Chờ xác nhận");
        reservation.setCreatedAt(LocalDateTime.now());

        // Cập nhật trạng thái bàn
        Optional<RestaurantTable> table = tableRepository.findById(reservation.getTableId());
        if (table.isPresent()) {
            table.get().setStatus("Đã đặt");
            tableRepository.save(table.get());
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Lấy danh sách đặt bàn của user
     */
    public List<Reservation> getUserReservations(Integer userId) {
        return reservationRepository.findUserReservationsDesc(userId);
    }

    /**
     * Chi tiết đặt bàn
     */
    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id).orElse(null);
    }

    /**
     * Cập nhật đặt bàn
     */
    @Transactional
    public Reservation updateReservation(Integer id, Reservation updatedReservation) {
        Optional<Reservation> existingOpt = reservationRepository.findById(id);

        if (existingOpt.isPresent()) {
            Reservation existing = existingOpt.get();

            // Kiểm tra nếu đổi bàn hoặc thời gian
            if (!existing.getTableId().equals(updatedReservation.getTableId()) ||
                    !existing.getReservationDate().equals(updatedReservation.getReservationDate()) ||
                    !existing.getReservationTime().equals(updatedReservation.getReservationTime())) {

                if (!isTableAvailable(updatedReservation.getTableId(),
                        updatedReservation.getReservationDate(),
                        updatedReservation.getReservationTime())) {
                    throw new RuntimeException("Table is not available at this time");
                }
            }

            existing.setTableId(updatedReservation.getTableId());
            existing.setReservationDate(updatedReservation.getReservationDate());
            existing.setReservationTime(updatedReservation.getReservationTime());
            existing.setGuestCount(updatedReservation.getGuestCount());
            existing.setNote(updatedReservation.getNote());

            return reservationRepository.save(existing);
        }

        return null;
    }

    /**
     * Hủy đặt bàn
     */
    @Transactional
    public boolean cancelReservation(Integer id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);

        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            res.setStatus("Đã hủy");

            // Cập nhật trạng thái bàn
            Optional<RestaurantTable> table = tableRepository.findById(res.getTableId());
            if (table.isPresent()) {
                table.get().setStatus("Trống");
                tableRepository.save(table.get());
            }

            reservationRepository.save(res);
            return true;
        }

        return false;
    }

    /**
     * Check-in bằng QR code
     */
    @Transactional
    public Reservation checkInWithQR(String qrCode) {
        Optional<Reservation> reservation = reservationRepository.findByQrCode(qrCode);

        if (reservation.isPresent()) {
            Reservation res = reservation.get();

            if (res.getStatus().equals("Đã xác nhận")) {
                res.setStatus("Hoàn thành");

                // Cập nhật trạng thái bàn
                Optional<RestaurantTable> table = tableRepository.findById(res.getTableId());
                if (table.isPresent()) {
                    table.get().setStatus("Đang sử dụng");
                    tableRepository.save(table.get());
                }

                return reservationRepository.save(res);
            }
        }

        return null;
    }

    /**
     * Tạo mã QR
     */
    private String generateQRCode() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}