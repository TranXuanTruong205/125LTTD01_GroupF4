package com.dine.DINERestaurant_Backend.notifications.repository;

import com.dine.DINERestaurant_Backend.notifications.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Thông báo của 1 user, mới nhất trước
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Đánh dấu tất cả thông báo của user
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
