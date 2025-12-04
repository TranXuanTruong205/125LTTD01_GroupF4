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
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {

    List<RestaurantTable> findByStatus(String status);

    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);

    Optional<RestaurantTable> findByTableNumber(String tableNumber);
}