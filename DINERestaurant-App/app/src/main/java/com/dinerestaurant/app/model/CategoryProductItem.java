package com.dinerestaurant.app.model;

import com.google.gson.annotations.SerializedName;

public class CategoryProductItem {

    @SerializedName("itemId")
    private int itemId;

    @SerializedName("category")
    private CategoryItem category; // nếu BE trả nguyên Category, còn nếu chỉ id thì sửa lại

    @SerializedName("itemName")
    private String itemName;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("discountPrice")
    private Double discountPrice;

    @SerializedName("image")
    private String image;

    @SerializedName("rating")
    private Double rating;

    @SerializedName("totalReviews")
    private Integer totalReviews;

    @SerializedName("isAvailable")
    private Boolean isAvailable;

    public CategoryProductItem() {
    }

    // Getter/Setter chuẩn (cần để chỗ khác dùng)
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public CategoryItem getCategory() { return category; }
    public void setCategory(CategoryItem category) { this.category = category; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPriceRaw() { return price; }
    public void setPriceRaw(double price) { this.price = price; }

    public Double getDiscountPriceRaw() { return discountPrice; }
    public void setDiscountPriceRaw(Double discountPrice) { this.discountPrice = discountPrice; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Double getRatingRaw() { return rating; }
    public void setRatingRaw(Double rating) { this.rating = rating; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Boolean getAvailable() { return isAvailable; }
    public void setAvailable(Boolean available) { isAvailable = available; }

    // API cũ cho UI
    public String getImagePath() { return image; }

    public String getName() { return itemName; }

    public double getRating() { return rating != null ? rating : 0.0; }

    public String getOriginalPrice() { return String.valueOf(price); }

    public String getDiscountPrice() {
        return discountPrice == null ? null : String.valueOf(discountPrice);
    }
}
