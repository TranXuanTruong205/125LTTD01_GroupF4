package com.dinerestaurant.app.model;

import com.google.gson.annotations.SerializedName;

public class CategoryItem {

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("icon")
    private String icon;

    @SerializedName("displayOrder")
    private Integer displayOrder;

    public CategoryItem() {
    }

    // Getter/Setter chuẩn
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    // API cũ cho UI
    public String getName() { return categoryName; }

    public String getImagePath() {
        // nếu icon là tên file trong assets thì xử lý ở đây sau
        return icon;
    }
}
