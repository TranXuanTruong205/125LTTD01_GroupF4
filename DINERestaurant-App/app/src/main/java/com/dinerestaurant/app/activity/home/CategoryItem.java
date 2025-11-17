package com.dinerestaurant.app.activity.home;

public class CategoryItem {
    private String imagePath; // Đường dẫn ảnh trong assets
    private String name;

    public CategoryItem(String imagePath, String name) {
        this.imagePath = imagePath;
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }
}
