package com.dinerestaurant.app.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartItem {
    @SerializedName("cartItemId") private int cartItemId;
    @SerializedName("quantity") private int quantity;
    @SerializedName("menuItem") private CartMenuItem menuItem;
    @SerializedName("options") private List<ItemOption> options;
    public int getCartItemId() { return cartItemId; }
    public int getQuantity() { return quantity; }
    public CartMenuItem getMenuItem() { return menuItem; }
    public List<ItemOption> getOptions() { return options; }
}