package com.dinerestaurant.app.activity.other;

public class LikedProductItem {
    private String imagePath;
    private String name;
    private double rating;
    private String originalPrice;
    private String discountPrice;

    public LikedProductItem(String imagePath, String name, double rating, String originalPrice, String discountPrice) {
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

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }
}
