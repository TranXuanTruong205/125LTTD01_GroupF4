package com.dinerestaurant.app.data.remote.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApi {

    // Lấy danh sách đơn hàng của user (token được AuthInterceptor tự thêm)
    @GET("api/orders/my")
    Call<Map<String, Object>> getMyOrders();

    // Xem chi tiết đơn hàng
    @GET("api/orders/{id}")
    Call<Map<String, Object>> getOrderById(@Path("id") int orderId);

    // Lấy trạng thái đơn hàng
    @GET("api/orders/{id}/status")
    Call<Map<String, Object>> getOrderStatus(@Path("id") int orderId);

    // Hủy đơn hàng
    @PUT("api/orders/{id}/cancel")
    Call<Map<String, Object>> cancelOrder(@Path("id") int orderId);
}
