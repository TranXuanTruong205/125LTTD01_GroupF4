package com.dinerestaurant.app.data.repository;

import android.content.Context;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.OrderApi;

import java.util.Map;

import retrofit2.Call;

public class OrderRepository {

    private final OrderApi api;

    public OrderRepository(Context context) {
        this.api = ApiClient.getOrderApi();
    }

    // Lấy danh sách đơn hàng của user
    public Call<Map<String, Object>> getMyOrders() {
        return api.getMyOrders();
    }

    // Xem chi tiết đơn hàng
    public Call<Map<String, Object>> getOrderById(int orderId) {
        return api.getOrderById(orderId);
    }

    // Lấy trạng thái đơn hàng
    public Call<Map<String, Object>> getOrderStatus(int orderId) {
        return api.getOrderStatus(orderId);
    }

    // Hủy đơn hàng
    public Call<Map<String, Object>> cancelOrder(int orderId) {
        return api.cancelOrder(orderId);
    }
}
