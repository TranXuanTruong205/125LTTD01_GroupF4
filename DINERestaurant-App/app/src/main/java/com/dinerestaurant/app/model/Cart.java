package com.dinerestaurant.app.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Cart {
    @SerializedName("cartId") private int cartId;
    @SerializedName("cartItems") private List<CartItem> cartItems;
    @SerializedName("totalAmount") private double totalAmount;

    public List<CartItem> getCartItems() { return cartItems; }
    public double getTotalAmount() { return totalAmount; }

    public int getCartId() {
        return cartId;
    }
}