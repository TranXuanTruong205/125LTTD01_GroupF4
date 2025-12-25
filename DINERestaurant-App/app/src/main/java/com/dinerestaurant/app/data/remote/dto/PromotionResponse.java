package com.dinerestaurant.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class PromotionResponse {

    private int id;
    private String title;
    private String description;
    private String image;

    @SerializedName("discountPercent")
    private int discountPercent;

    private String startDate;
    private String endDate;
    private boolean isActive;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public boolean isActive() {
        return isActive;
    }
}
