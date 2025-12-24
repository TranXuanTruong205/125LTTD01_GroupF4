package com.dinerestaurant.app.data.remote.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReservationApi {

    @GET("reservations/available")
    Call<Map<String, Object>> getAvailableTables(
            @Query("date") String date,
            @Query("time") String time,
            @Query("guestCount") int guestCount
    );

    @POST("reservations")
    Call<Map<String, Object>> createReservation(@Body Map<String, Object> request);
}