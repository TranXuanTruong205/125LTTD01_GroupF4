package com.dinerestaurant.app.activity.home;

public class SpecialOfferItem {
    private String name;
    private double rating;
    private double originalPrice;
    private double discountPrice;

    public SpecialOfferItem(String name, double rating, double originalPrice, double discountPrice) {
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

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }
}
