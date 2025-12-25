package com.dinerestaurant.app.data.remote.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationApi {

        // =====================================================
        // KIỂM TRA BÀN TRỐNG
        // =====================================================

        /**
         * Kiểm tra danh sách bàn trống theo ngày, giờ và số khách
         * GET /api/reservations/available?date=2024-11-15&time=18:00&guestCount=4
         */
        @GET("api/reservations/available")
        Call<Map<String, Object>> getAvailableTables(
                        @Query("date") String date,
                        @Query("time") String time,
                        @Query("guestCount") int guestCount);

        // =====================================================
        // ĐẶT BÀN MỚI
        // =====================================================

        /**
         * Tạo đặt bàn mới
         * POST /api/reservations
         * Body: { tableId, date, time, guestCount, note }
         */
        @POST("api/reservations")
        Call<Map<String, Object>> createReservation(@Body Map<String, Object> request);

        // =====================================================
        // XEM LỊCH ĐẶT BÀN
        // =====================================================

        /**
         * Lấy danh sách đặt bàn của user hiện tại
         * GET /api/reservations/my
         */
        @GET("api/reservations/my")
        Call<Map<String, Object>> getMyReservations();

        /**
         * Chi tiết đặt bàn theo ID
         * GET /api/reservations/{id}
         */
        @GET("api/reservations/{id}")
        Call<Map<String, Object>> getReservationById(@Path("id") int reservationId);

        // =====================================================
        // SỬA ĐẶT BÀN
        // =====================================================

        /**
         * Cập nhật thông tin đặt bàn
         * PUT /api/reservations/{id}
         * Body: { tableId, date, time, guestCount, note }
         */
        @PUT("api/reservations/{id}")
        Call<Map<String, Object>> updateReservation(
                        @Path("id") int reservationId,
                        @Body Map<String, Object> request);

        // =====================================================
        // HỦY ĐẶT BÀN
        // =====================================================

        /**
         * Hủy đặt bàn theo ID
         * PUT /api/reservations/{id}/cancel
         */
        @PUT("api/reservations/{id}/cancel")
        Call<Map<String, Object>> cancelReservation(@Path("id") int reservationId);

        // =====================================================
        // CHECK-IN QR
        // =====================================================

        /**
         * Check-in bằng QR code
         * POST /api/reservations/checkin
         * Body: { qrCode: "RESERVATION:123" }
         */
        @POST("api/reservations/checkin")
        Call<Map<String, Object>> checkInWithQR(@Body Map<String, String> request);
}