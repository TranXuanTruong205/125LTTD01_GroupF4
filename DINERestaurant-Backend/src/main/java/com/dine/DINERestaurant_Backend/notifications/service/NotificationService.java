package com.dine.DINERestaurant_Backend.notifications.service;

import com.dine.DINERestaurant_Backend.notifications.DTO.NotificationResponse;
import com.dine.DINERestaurant_Backend.notifications.Entity.Notification;
import com.dine.DINERestaurant_Backend.notifications.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // ===========================
    // Lấy danh sách thông báo của user hiện tại
    // ===========================
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        List<Notification> list = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===========================
    // Đánh dấu 1 thông báo là đã đọc
    // ===========================
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));

        // Chặn user khác
        if (notification.getUserId() == null ||
                !notification.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thể sửa thông báo của người khác"
            );
        }

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            // đã đọc rồi thì cứ trả về luôn, không cần lỗi
            return mapToResponse(notification);
        }

        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    // ===========================
    // Đánh dấu TẤT CẢ thông báo của user là đã đọc
    // ===========================
    public void markAllAsRead(Long userId) {
        List<Notification> unreadList = notificationRepository
                .findByUserIdAndIsReadFalse(userId);

        for (Notification n : unreadList) {
            n.setIsRead(true);
        }

        notificationRepository.saveAll(unreadList);
    }

    private NotificationResponse mapToResponse(Notification n) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(Boolean.TRUE.equals(n.getIsRead()));
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
