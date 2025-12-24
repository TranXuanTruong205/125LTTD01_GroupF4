package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.data.remote.dto.CategoryDto;
import com.dinerestaurant.app.data.remote.dto.MenuItemDto;
import com.dinerestaurant.app.model.ItemOption;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // ...... Auth endpoints (nếu có) ......

    // Categories
    @GET("api/categories")
    Call<List<CategoryDto>> getCategories();

    // MenuItems theo category
    @GET("api/menu-items/category/{categoryId}")
    Call<List<MenuItemDto>> getMenuItemsByCategory(@Path("categoryId") int categoryId);
    @GET("api/menu-items/{itemId}/options")
    Call<List<ItemOption>> getMenuOptions(@Path("itemId") int itemId);
}
