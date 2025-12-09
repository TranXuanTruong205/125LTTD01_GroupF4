package com.dinerestaurant.app.data.remote.dto;

import java.io.Serializable;

public class MenuItemDto implements Serializable {
    private int itemId;
    private String itemName;
    private String description;
    private double price;
    private Double discountPrice;
    private Double rating;
    private Integer totalReviews;
    private Boolean available;
    private String image; // tên/đường dẫn ảnh BE lưu
    private CategoryDto category; // có thể null nếu BE không trả

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public Double getDiscountPrice() { return discountPrice; }
    public Double getRating() { return rating; }
    public Integer getTotalReviews() { return totalReviews; }
    public Boolean getAvailable() { return available; }
    public String getImage() { return image; }
    public CategoryDto getCategory() { return category; }
}
