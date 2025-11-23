package com.dinerestaurant.app.model.home;

public class CategoryProductItem {
    private String imagePath;
    private String name;
    private double rating;
    private String originalPrice;
    private String discountPrice;

    public CategoryProductItem(String imagePath, String name, double rating, String originalPrice, String discountPrice) {
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
