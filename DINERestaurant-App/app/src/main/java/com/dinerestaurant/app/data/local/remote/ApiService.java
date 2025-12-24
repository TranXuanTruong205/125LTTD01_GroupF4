package com.dinerestaurant.app.data.local.remote;

import com.dinerestaurant.app.model.NotificationItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Lấy danh sách thông báo
    @GET("api/notifications")
    Call<List<NotificationItem>> getNotifications(
            @Header("Authorization") String bearerToken
    );

    // Đánh dấu 1 thông báo đã đọc
    @PUT("api/notifications/{id}/read")
    Call<Void> markNotificationRead(
            @Header("Authorization") String bearerToken,
            @Path("id") int notificationId
    );

    // Đánh dấu tất cả đã đọc
    @PUT("api/notifications/read-all")
    Call<Void> markAllNotificationsRead(
            @Header("Authorization") String bearerToken
    );
}
