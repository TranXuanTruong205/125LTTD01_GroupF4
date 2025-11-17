package com.dinerestaurant.app.activity.home;

public class CategoryItem {
    private String icon;
    private String name;

    public CategoryItem(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
