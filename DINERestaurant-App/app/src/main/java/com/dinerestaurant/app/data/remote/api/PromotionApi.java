package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.data.remote.dto.PromotionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PromotionApi {
    @GET("api/promotions")
    Call<List<PromotionResponse>> getPromotions();
}
