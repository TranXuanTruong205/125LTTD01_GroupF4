package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.data.remote.dto.ReviewResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReviewApi {

    @GET("api/reviews/item/{itemId}")
    Call<List<ReviewResponse>> getReviewsByItem(@Path("itemId") int itemId);
}
