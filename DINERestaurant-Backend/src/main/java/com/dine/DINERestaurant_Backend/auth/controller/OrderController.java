package com.dine.DINERestaurant_Backend.auth.controller;

import com.dine.DINERestaurant_Backend.auth.entity.Order;
import com.dine.DINERestaurant_Backend.auth.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ==================== ĐẶT ĐƠN HÀNG (3 loại) ====================

    @PostMapping("/onsite")
    public ResponseEntity<Map<String, Object>> createOnsiteOrder(@RequestBody Map<String, Object> request) {
        return createOrderResponse(request, "Tại chỗ");
    }

    @PostMapping("/delivery")
    public ResponseEntity<Map<String, Object>> createDeliveryOrder(@RequestBody Map<String, Object> request) {
        return createOrderResponse(request, "Giao hàng");
    }

    @PostMapping("/pickup")
    public ResponseEntity<Map<String, Object>> createPickupOrder(@RequestBody Map<String, Object> request) {
        return createOrderResponse(request, "Mang về");
    }

    // Hàm chung cho 3 loại đơn hàng
    private ResponseEntity<Map<String, Object>> createOrderResponse(Map<String, Object> request, String orderType) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.createOrder(request, orderType);

            response.put("success", true);
            response.put("message", "Đặt hàng thành công");
            response.put("data", order);                    // Trả thẳng Entity
            response.put("orderNumber", orderService.generateOrderNumber(order.getOrderId()));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== CÁC API KHÁC (giữ format cũ, trả Entity trong data) ====================

    @GetMapping("/my/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            response.put("success", true);
            response.put("data", orders);         // Trả thẳng List<Order>
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        Order order = orderService.getOrderById(id);

        if (order != null) {
            response.put("success", true);
            response.put("data", order); // Trả thẳng Entity
            response.put("orderNumber", orderService.generateOrderNumber(id));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Đơn hàng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        String status = orderService.getOrderStatus(id);

        if (status != null) {
            response.put("success", true);
            response.put("status", status);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Đơn hàng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        boolean cancelled = orderService.cancelOrder(id);

        if (cancelled) {
            response.put("success", true);
            response.put("message", "Đơn hàng đã được hủy thành công");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Không thể hủy đơn hàng (có thể đã hoàn thành)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable Integer id,
                                                                 @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        String newStatus = body.get("status");

        Order order = orderService.updateOrderStatus(id, newStatus);
        if (order != null) {
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thành công");
            response.put("data", order); // Trả thẳng Entity đã cập nhật
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Đơn hàng không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<Order> orders = orderService.getOrdersByStatus(status.equals("all") ? null : status);

        response.put("success", true);
        response.put("data", orders); // Trả thẳng List Entity
        return ResponseEntity.ok(response);
    }
}