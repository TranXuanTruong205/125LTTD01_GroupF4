package com.dinerestaurant.app.data.remote.dto;

public class MenuItemDto {

    private int itemId;
    private CategoryDto category;
    private String itemName;
    private String description;
    private double price;
    private Double discountPrice;
    private String image;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable; // Map field is_available / isAvailable của BE

    public int getItemId() {
        return itemId;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public String getImage() {
        return image;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }
}
