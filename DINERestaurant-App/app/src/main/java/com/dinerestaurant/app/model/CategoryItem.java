package com.dinerestaurant.app.model;

public class CategoryItem {
    private int id;           // categoryId từ BE
    private String imagePath; // đường dẫn ảnh (vẫn assets)
    private String name;      // categoryName

    public CategoryItem(int id, String imagePath, String name) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
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
}
