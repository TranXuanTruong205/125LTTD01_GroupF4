package com.dinerestaurant.app.model;

public class CategoryProductItem {
    private int id;          // itemId từ BE
    private String imagePath;
    private String name;
    private double rating;
    private double price;
    private Double discountPrice;

    public CategoryProductItem(int id,
                               String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    public CategoryProductItem(String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this(0, imagePath, name, rating, price, discountPrice);
    }

    public int getId() {
        return id;
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
