package com.dine.DINERestaurant_Backend.controller;

import com.dine.DINERestaurant_Backend.dto.OrderRequest;
import com.dine.DINERestaurant_Backend.entity.Order;
import com.dine.DINERestaurant_Backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * ✅ Đặt đơn tại bàn
     * POST /orders/onsite
     */
    @PostMapping("/onsite")
    public ResponseEntity<Map<String, Object>> createOnsiteOrder(@RequestBody OrderRequest request) {
        return createOrderResponse(() -> orderService.createOnsiteOrder(request));
    }

    /**
     * ✅ Đặt đơn giao hàng
     * POST /orders/delivery
     */
    @PostMapping("/delivery")
    public ResponseEntity<Map<String, Object>> createDeliveryOrder(@RequestBody OrderRequest request) {
        return createOrderResponse(() -> orderService.createDeliveryOrder(request));
    }

    /**
     * ✅ Đặt đơn mang về
     * POST /orders/pickup
     */
    @PostMapping("/pickup")
    public ResponseEntity<Map<String, Object>> createPickupOrder(@RequestBody OrderRequest request) {
        return createOrderResponse(() -> orderService.createPickupOrder(request));
    }

    /**
     * ✅ Lịch sử đơn hàng
     * GET /orders/my/{userId}
     */
    @GetMapping("/my/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            response.put("success", true);
            response.put("data", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ Chi tiết đơn hàng
     * GET /orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                response.put("success", true);
                response.put("data", order);
                response.put("orderNumber", orderService.generateOrderNumber(id));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ Theo dõi trạng thái
     * GET /orders/{id}/status
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            String status = orderService.getOrderStatus(id);
            if (status != null) {
                response.put("success", true);
                response.put("status", status);
                response.put("orderId", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ Hủy đơn hàng
     * PUT /orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = orderService.cancelOrder(id);
            if (success) {
                response.put("success", true);
                response.put("message", "Order cancelled successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Cannot cancel this order");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng (Admin)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String newStatus = request.get("status");
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);

            if (updatedOrder != null) {
                response.put("success", true);
                response.put("message", "Order status updated");
                response.put("data", updatedOrder);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lấy đơn hàng theo trạng thái (Admin)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper method để tạo response
     */
    private ResponseEntity<Map<String, Object>> createOrderResponse(OrderCreator creator) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = creator.create();
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("data", order);
            response.put("orderNumber", orderService.generateOrderNumber(order.getOrderId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @FunctionalInterface
    interface OrderCreator {
        Order create();
    }
}