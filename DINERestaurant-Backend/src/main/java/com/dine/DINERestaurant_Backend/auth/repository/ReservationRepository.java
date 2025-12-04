package com.dine.DINERestaurant_Backend.auth.repository;

import com.dine.DINERestaurant_Backend.auth.entity.Reservation;
import com.dine.DINERestaurant_Backend.auth.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUserId(Integer userId);

    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.reservationDate DESC, r.reservationTime DESC")
    List<Reservation> findUserReservationsDesc(Integer userId);

    @Query("SELECT r FROM Reservation r WHERE r.tableId = :tableId AND r.reservationDate = :date AND r.status != 'Đã hủy'")
    List<Reservation> findByTableAndDate(Integer tableId, LocalDate date);

    Optional<Reservation> findByQrCode(String qrCode);
}