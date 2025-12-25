package com.dinerestaurant.app.data.remote.api;

import com.dinerestaurant.app.model.NotificationItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface ApiNotification {

    @GET("api/notifications")
    Call<List<NotificationItem>> getNotifications();

    @PUT("api/notifications/{id}/read")
    Call<Void> markNotificationRead(@Path("id") int notificationId);

    @PUT("api/notifications/read-all")
    Call<Void> markAllNotificationsRead();
}
