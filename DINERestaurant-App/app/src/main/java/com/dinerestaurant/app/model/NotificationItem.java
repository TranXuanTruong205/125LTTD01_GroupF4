package com.dinerestaurant.app.model;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {


    @SerializedName("id")  // **SỬA từ "notification_id" thành "id"**
    private int notificationId;


    private Integer userId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    // "Đơn hàng", "Khuyến mãi", "Đặt bàn", "Hệ thống"
    @SerializedName("type")
    private String type;

    // ⭐ ĐỔI is_read -> read
    @SerializedName("read")
    private boolean isRead;

    @SerializedName("createdAt")
    private String createdAt;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
