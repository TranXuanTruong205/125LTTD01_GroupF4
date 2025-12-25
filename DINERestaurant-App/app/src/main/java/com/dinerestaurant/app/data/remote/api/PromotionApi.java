package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.data.remote.dto.ApplyPromotionRequest;
import com.dinerestaurant.app.data.remote.dto.ApplyPromotionResponse;
import com.dinerestaurant.app.data.remote.dto.PromotionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PromotionApi {
    @GET("api/promotions")
    Call<List<PromotionResponse>> getPromotions();
    @POST("api/promotions/apply")
    Call<ApplyPromotionResponse> applyPromotion(
            @Body ApplyPromotionRequest request
    );


}
