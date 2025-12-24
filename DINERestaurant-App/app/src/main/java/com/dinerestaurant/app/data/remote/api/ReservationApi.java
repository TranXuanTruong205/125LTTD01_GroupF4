package com.dinerestaurant.app.data.remote.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationApi {

    // Lấy danh sách bàn trống
    @GET("api/reservations/available")
    Call<Map<String, Object>> getAvailableTables(
            @Query("date") String date,
            @Query("time") String time,
            @Query("guestCount") int guestCount);

    // Tạo đặt bàn mới
    @POST("api/reservations")
    Call<Map<String, Object>> createReservation(
            @Header("Authorization") String token,
            @Body Map<String, Object> request);

    // Lấy danh sách đặt bàn của user
    @GET("api/reservations/my")
    Call<Map<String, Object>> getMyReservations(
            @Header("Authorization") String token);

    // Hủy đặt bàn
    @PUT("api/reservations/{id}/cancel")
    Call<Map<String, Object>> cancelReservation(
            @Header("Authorization") String token,
            @Path("id") int reservationId);
}