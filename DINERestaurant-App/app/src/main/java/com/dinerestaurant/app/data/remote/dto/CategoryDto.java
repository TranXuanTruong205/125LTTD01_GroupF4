package com.dinerestaurant.app.data.remote.dto;

public class CategoryDto {
    private int categoryId;
    private String categoryName;
    private String icon;
    private Integer displayOrder;

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}
