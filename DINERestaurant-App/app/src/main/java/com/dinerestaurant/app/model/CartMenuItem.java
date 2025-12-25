package com.dinerestaurant.app.model;
import com.google.gson.annotations.SerializedName;

public class CartMenuItem {
    @SerializedName("itemId") private int itemId;
    @SerializedName("itemName") private String itemName;
    @SerializedName("price") private double price;
    @SerializedName("image") private String image;

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
}