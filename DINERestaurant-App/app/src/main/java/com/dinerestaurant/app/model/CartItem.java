package com.dinerestaurant.app.model;
import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("cartItemId") private int cartItemId;
    @SerializedName("quantity") private int quantity;
    @SerializedName("menuItem") private CartMenuItem menuItem;

    public int getCartItemId() { return cartItemId; }
    public int getQuantity() { return quantity; }
    public CartMenuItem getMenuItem() { return menuItem; }
}