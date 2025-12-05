package com.dine.DINERestaurant_Backend.order.repository;

import com.dine.DINERestaurant_Backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    List<Order> findByOrderStatus(String orderStatus);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersDesc(Integer userId);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.orderStatus = :status")
    List<Order> findByUserIdAndStatus(Integer userId, String status);
}