package com.dinerestaurant.app.data.remote.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    /**
     * Checkout từ giỏ hàng - tạo đơn hàng mới
     * Body: {
     * "cartItemIds": [1, 2, 3],
     * "orderType": "Tại chỗ" | "Mang về" | "Giao hàng",
     * "tableId": 1 (optional),
     * "addressId": 1 (optional),
     * "paymentMethod": "Cash" | "Momo" | ...,
     * "note": "Ghi chú" (optional)
     * }
     */
    @POST("api/orders/checkout")
    Call<Map<String, Object>> checkout(@Body Map<String, Object> request);

    /**
     * Đặt đơn ăn tại chỗ (dine-in)
     * Body: { cartItemIds, tableId, paymentMethod, note }
     */
    @POST("api/orders/onsite")
    Call<Map<String, Object>> createOnsiteOrder(@Body Map<String, Object> request);

    /**
     * Đặt đơn giao hàng (delivery)
     * Body: { cartItemIds, addressId, paymentMethod, note }
     */
    @POST("api/orders/delivery")
    Call<Map<String, Object>> createDeliveryOrder(@Body Map<String, Object> request);

    /**
     * Đặt đơn mang về (pickup/takeaway)
     * Body: { cartItemIds, tableId (optional), paymentMethod, note }
     */
    @POST("api/orders/pickup")
    Call<Map<String, Object>> createPickupOrder(@Body Map<String, Object> request);
}
