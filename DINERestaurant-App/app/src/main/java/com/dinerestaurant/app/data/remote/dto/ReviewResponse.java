package com.dinerestaurant.app.data.remote.dto;

public class ReviewResponse {
    public int id;
    public int userId;
    public int itemId;
    public int orderId;
    public int rating;
    public String comment;
    public boolean verifiedPurchase;
    public String createdAt;
    public String userName;

    // (Getter là optional, nhưng để cho gọn gàng)
    public String getUserName() {
        return userName;
    }
}
