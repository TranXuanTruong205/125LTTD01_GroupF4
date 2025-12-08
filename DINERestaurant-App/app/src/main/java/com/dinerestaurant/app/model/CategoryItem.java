package com.dinerestaurant.app.model;

public class CategoryItem {
    private int id;
    private String name;
    private String imagePath;

    public CategoryItem(int id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
}

