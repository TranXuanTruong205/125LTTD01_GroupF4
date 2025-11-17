package com.dinerestaurant.app.activity.other;

public class LikedProductItem {
    private String name;
    private double rating;
    private String originalPrice;
    private String discountPrice;

    public LikedProductItem(String name, double rating, String originalPrice, String discountPrice) {
        this.name = name;
        this.rating = rating;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
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
