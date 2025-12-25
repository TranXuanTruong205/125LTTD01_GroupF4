package com.dinerestaurant.app.model;

public class CategoryItem {
    private int id;
    private String name;
    private String imagePath;

    // Dùng cho dữ liệu từ BE
    public CategoryItem(int id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    // Dùng cho dữ liệu tĩnh cũ (CategoryFragment, HomeFragment cũ)
    // imagePath, name -> id mặc định -1
    public CategoryItem(String imagePath, String name) {
        this(-1, name, imagePath);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
}
