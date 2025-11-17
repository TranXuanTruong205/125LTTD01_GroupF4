package com.dinerestaurant.app.activity.home;

public class SpecialOfferItem {
    private String imagePath;
    private String name;
    private double rating;
    private double originalPrice;
    private double discountPrice;

    // Constructor đơn giản cho việc hiển thị ảnh
    public SpecialOfferItem(String imagePath, String name) {
        this.imagePath = imagePath;
        this.name = name;
        this.rating = 4.5;
        this.originalPrice = 0;
        this.discountPrice = 0;
    }

    // Constructor đầy đủ
    public SpecialOfferItem(String imagePath, String name, double rating, double originalPrice, double discountPrice) {
        this.imagePath = imagePath;
        this.name = name;
        this.rating = rating;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }
}
