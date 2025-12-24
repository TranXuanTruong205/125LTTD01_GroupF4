package com.dinerestaurant.app.data.repository;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ReservationApi;
import com.dinerestaurant.app.data.remote.api.ApiClient;

import java.util.Map;

import retrofit2.Call;

public class ReservationRepository {

    private final ReservationApi api;

    public ReservationRepository() {
        this.api = ApiClient.getReservationApi();
    }

    public Call<Map<String, Object>> getAvailableTables(String date, String time, int guestCount) {
        return api.getAvailableTables(date, time, guestCount);
    }

    public Call<Map<String, Object>> createReservation(Map<String, Object> request) {
        return api.createReservation(request);
    }
}