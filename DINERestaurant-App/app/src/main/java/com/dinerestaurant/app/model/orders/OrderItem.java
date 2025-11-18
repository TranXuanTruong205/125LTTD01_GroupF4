package com.dinerestaurant.app.model.orders;

public class OrderItem {
    private String orderId;
    private String foodName;
    private double price;
    private int rating; // 1-5 sao
    private String status; // Active, Completed, Cancelled
    private int foodImage; // Resource ID cho avatar
    private boolean showStepper; // Có hiển thị stepper không

    public OrderItem(String orderId, String foodName, double price, int rating, String status, int foodImage) {
        this.orderId = orderId;
        this.foodName = foodName;
        this.price = price;
        this.rating = rating;
        this.status = status;
        this.foodImage = foodImage;
        this.showStepper = false; // Mặc định không hiện
    }

    // Constructor với showStepper
    public OrderItem(String orderId, String foodName, double price, int rating, String status, int foodImage, boolean showStepper) {
        this.orderId = orderId;
        this.foodName = foodName;
        this.price = price;
        this.rating = rating;
        this.status = status;
        this.foodImage = foodImage;
        this.showStepper = showStepper;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getPrice() {
        return price;
    }

    public int getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }

    public int getFoodImage() {
        return foodImage;
    }

    public boolean isShowStepper() {
        return showStepper;
    }

    public void setShowStepper(boolean showStepper) {
        this.showStepper = showStepper;
    }

    public String getFormattedPrice() {
        return "£ " + String.format("%.2f", price);
    }
}