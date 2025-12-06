package com.dine.DINERestaurant_Backend.reservation.repository;

import com.dine.DINERestaurant_Backend.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUserId(Integer userId);

    List<Reservation> findByStatus(String status);

    // Lịch đặt bàn của user (mới nhất → cũ nhất)
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.userId = :userId " +
            "ORDER BY r.reservationDate DESC, r.reservationTime DESC")
    List<Reservation> findUserReservationsDesc(@Param("userId") Integer userId);

    // Lấy danh sách đặt bàn theo table + date
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.tableId = :tableId " +
            "AND r.reservationDate = :date " +
            "AND r.status <> 'Đã hủy'")
    List<Reservation> findByTableAndDate(
            @Param("tableId") Integer tableId,
            @Param("date") LocalDate date
    );

    Optional<Reservation> findByQrCode(String qrCode);
}
