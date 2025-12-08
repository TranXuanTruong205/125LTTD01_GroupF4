package com.dinerestaurant.app.model;

public class CategoryProductItem {
    private int id;             // itemId từ BE
    private int categoryId;     // categoryId từ BE
    private String imagePath;   // ảnh UI (có thể assets hoặc URL sau này)
    private String name;
    private double rating;
    private double price;       // giá gốc
    private Double discountPrice; // giá giảm (có thể null)

    public CategoryProductItem(int id,
                               int categoryId,
                               String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this.id = id;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
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

    public double getPrice() {
        return price;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }
}
