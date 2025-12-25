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

    // =====================================================
    // XEM ĐƠN HÀNG
    // =====================================================

    /**
     * Lấy danh sách đơn hàng của user
     * GET /api/orders/my
     */
    public Call<Map<String, Object>> getMyOrders() {
        return api.getMyOrders();
    }

    /**
     * Xem chi tiết đơn hàng theo ID
     * GET /api/orders/{id}
     */
    public Call<Map<String, Object>> getOrderById(int orderId) {
        return api.getOrderById(orderId);
    }

    /**
     * Lấy trạng thái đơn hàng
     * GET /api/orders/{id}/status
     */
    public Call<Map<String, Object>> getOrderStatus(int orderId) {
        return api.getOrderStatus(orderId);
    }

    // =====================================================
    // HỦY ĐƠN HÀNG
    // =====================================================

    /**
     * Hủy đơn hàng
     * PUT /api/orders/{id}/cancel
     */
    public Call<Map<String, Object>> cancelOrder(int orderId) {
        return api.cancelOrder(orderId);
    }

    // =====================================================
    // TẠO ĐƠN HÀNG
    // =====================================================

    /**
     * Checkout từ giỏ hàng (generic)
     * POST /api/orders/checkout
     */
    public Call<Map<String, Object>> checkout(Map<String, Object> request) {
        return api.checkout(request);
    }

    /**
     * Tạo đơn ăn tại chỗ (dine-in)
     * POST /api/orders/onsite
     */
    public Call<Map<String, Object>> createOnsiteOrder(Map<String, Object> request) {
        return api.createOnsiteOrder(request);
    }

    /**
     * Tạo đơn giao hàng (delivery)
     * POST /api/orders/delivery
     */
    public Call<Map<String, Object>> createDeliveryOrder(Map<String, Object> request) {
        return api.createDeliveryOrder(request);
    }

    /**
     * Tạo đơn mang về (pickup/takeaway)
     * POST /api/orders/pickup
     */
    public Call<Map<String, Object>> createPickupOrder(Map<String, Object> request) {
        return api.createPickupOrder(request);
    }
}
