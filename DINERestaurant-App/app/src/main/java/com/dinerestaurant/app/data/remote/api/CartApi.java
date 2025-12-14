package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.model.Cart;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {

    @GET("api/cart")
    Call<Cart> getCart();

    @PUT("api/cart/update")
    Call<Cart> updateQuantity(@Body Map<String, Object> body);

    @DELETE("api/cart/items/{id}")
    Call<Void> removeItem(@Path("id") int cartItemId);

    @DELETE("api/cart/clear")
    Call<Void> clearCart();
}