package com.dinerestaurant.app.model;
import java.io.Serializable;
public class CategoryProductItem implements  Serializable {

    private int itemId;
    private String imagePath;
    private String name;
    private double rating;
    private double price;
    private Double discountPrice; // có thể null

    public CategoryProductItem(int itemId,
                               String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this.itemId = itemId;
        this.imagePath = imagePath;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    public int getItemId() {
        return itemId;
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

    // Giá hiển thị (nếu có giảm thì dùng giảm, không thì dùng price)
    public double getDisplayPrice() {
        return discountPrice != null ? discountPrice : price;
    }
}
