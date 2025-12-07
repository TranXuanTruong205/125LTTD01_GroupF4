package com.dine.DINERestaurant_Backend.promotions.repository;

import com.dine.DINERestaurant_Backend.promotions.Entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

// CHANGED: JpaRepository<Promotion, Integer>
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.isActive = true " +
            "AND :today BETWEEN p.startDate AND p.endDate")
    List<Promotion> findActivePromotions(LocalDate today);
}
