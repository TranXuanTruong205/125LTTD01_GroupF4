package com.dinerestaurant.app.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ReservationApi;

import java.util.Map;

import retrofit2.Call;

public class ReservationRepository {

    private final ReservationApi api;
    private final Context context;

    public ReservationRepository(Context context) {
        this.context = context;
        this.api = ApiClient.getReservationApi();
    }

    private String getAuthToken() {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        return "Bearer " + token;
    }

    public Call<Map<String, Object>> getAvailableTables(String date, String time, int guestCount) {
        return api.getAvailableTables(date, time, guestCount);
    }

    public Call<Map<String, Object>> createReservation(Map<String, Object> request) {
        return api.createReservation(getAuthToken(), request);
    }

    public Call<Map<String, Object>> getMyReservations() {
        return api.getMyReservations(getAuthToken());
    }

    public Call<Map<String, Object>> cancelReservation(int reservationId) {
        return api.cancelReservation(getAuthToken(), reservationId);
    }
}