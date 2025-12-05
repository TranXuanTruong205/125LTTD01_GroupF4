package com.dine.DINERestaurant_Backend.reservation.repository;
import com.dine.DINERestaurant_Backend.reservation.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {

    List<RestaurantTable> findByStatus(String status);

    List<RestaurantTable> findByCapacityGreaterThanEqualAndStatus(Integer capacity, String status);

    Optional<RestaurantTable> findByTableNumber(String tableNumber);
}