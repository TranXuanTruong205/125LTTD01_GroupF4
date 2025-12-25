package com.dinerestaurant.app.data.repository;

import android.content.Context;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ReservationApi;

import java.util.Map;

import retrofit2.Call;

public class ReservationRepository {

    private final ReservationApi api;

    public ReservationRepository(Context context) {
        // Context được sử dụng để init ApiClient nếu cần
        this.api = ApiClient.getReservationApi();
    }

    public Call<Map<String, Object>> getAvailableTables(String date, String time, int guestCount) {
        return api.getAvailableTables(date, time, guestCount);
    }

    public Call<Map<String, Object>> createReservation(Map<String, Object> request) {
        return api.createReservation(request);
    }

    public Call<Map<String, Object>> getMyReservations() {
        return api.getMyReservations();
    }

    public Call<Map<String, Object>> cancelReservation(int reservationId) {
        return api.cancelReservation(reservationId);
    }

    public Call<Map<String, Object>> updateReservation(int reservationId, Map<String, Object> request) {
        return api.updateReservation(reservationId, request);
    }

    public Call<Map<String, Object>> getReservationById(int reservationId) {
        return api.getReservationById(reservationId);
    }

    public Call<Map<String, Object>> checkInWithQR(Map<String, String> request) {
        return api.checkInWithQR(request);
    }
}