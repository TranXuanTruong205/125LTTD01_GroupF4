package com.dinerestaurant.app.model;

public class CategoryProductItem {

    // id của món (menu_items.item_id trên BE)
    private int id;

    // đường dẫn ảnh (tạm dùng assets như cũ, sau này có thể là URL)
    private String imagePath;

    private String name;
    private double rating;

    // giá gốc
    private double price;

    // giá khuyến mãi (có thể null)
    private Double discountPrice;

    // Constructor đầy đủ (dùng khi map từ BE)
    public CategoryProductItem(int id,
                               String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this.id = id;
        this.imagePath = imagePath;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    // Constructor tiện cho dữ liệu fake cũ (không cần id)
    public CategoryProductItem(String imagePath,
                               String name,
                               double rating,
                               double price,
                               Double discountPrice) {
        this(0, imagePath, name, rating, price, discountPrice);
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

    public double getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }
}
