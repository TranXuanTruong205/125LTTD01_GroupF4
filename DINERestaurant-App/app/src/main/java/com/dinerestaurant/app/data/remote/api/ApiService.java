package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.data.remote.dto.CategoryDto;
import com.dinerestaurant.app.data.remote.dto.MenuItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // --- CATEGORY ---

    @GET("api/categories")
    Call<List<CategoryDto>> getCategories();

    // --- MENU ITEMS ---

    @GET("api/menu-items/category/{categoryId}")
    Call<List<MenuItemDto>> getMenuItemsByCategory(
            @Path("categoryId") int categoryId
    );
}
