package com.dine.DINERestaurant_Backend.notifications.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.notifications.DTO.NotificationResponse;
import com.dine.DINERestaurant_Backend.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7); // "Bearer xxx"
        String userId = jwtUtil.extractUserId(token);
        return Long.valueOf(userId);
    }

    // ===========================
    // GET /api/notifications
    // Lấy danh sách thông báo của user hiện tại
    // ===========================
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = getCurrentUserId(authHeader);
        List<NotificationResponse> list = notificationService.getNotificationsForUser(currentUserId);
        return ResponseEntity.ok(list);
    }

    // ===========================
    // PUT /api/notifications/{id}/read
    // Đánh dấu 1 thông báo đã đọc
    // ===========================
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") Long notificationId
    ) {
        Long currentUserId = getCurrentUserId(authHeader);
        NotificationResponse response = notificationService.markAsRead(currentUserId, notificationId);
        return ResponseEntity.ok(response);
    }

    // ===========================
    // PUT /api/notifications/read-all
    // Đánh dấu tất cả thông báo của user là đã đọc
    // ===========================
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long currentUserId = getCurrentUserId(authHeader);
        notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.noContent().build();
    }
}
