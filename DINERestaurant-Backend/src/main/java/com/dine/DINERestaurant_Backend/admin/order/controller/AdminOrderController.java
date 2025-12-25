package com.dine.DINERestaurant_Backend.admin.order.controller;

import com.dine.DINERestaurant_Backend.admin.order.service.AdminOrderService;
import com.dine.DINERestaurant_Backend.order.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "*")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    // ===============================
    // GET ALL ORDERS
    // GET /api/admin/orders
    // ===============================
    @GetMapping
    public ResponseEntity<List<Order>> listAll() {
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    // ===============================
    // GET ACTIVE ORDERS
    // GET /api/admin/orders/active
    // ===============================
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        return ResponseEntity.ok(adminOrderService.getOngoingOrders());
    }

    // ===============================
    // GET HISTORY
    // GET /api/admin/orders/history
    // ===============================
    @GetMapping("/history")
    public ResponseEntity<List<Order>> getHistory() {
        return ResponseEntity.ok(adminOrderService.getOrderHistory());
    }

    // ===============================
    // UPDATE STATUS
    // PUT /api/admin/orders/{id}/status
    // ===============================
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("status");
        Order updated = adminOrderService.changeStatus(id, newStatus);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    // ===============================
    // GET ORDER DETAIL
    // GET /api/admin/orders/{id}
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer id) {
        return adminOrderService.getOrderById(id)
                .map(order -> {
                    Map<String, Object> resp = new java.util.HashMap<>();
                    resp.put("orderId", order.getOrderId());
                    resp.put("orderStatus", order.getOrderStatus());
                    // Map status key cho CSS color
                    resp.put("orderStatusKey", order.getOrderStatus().toLowerCase().replace(" ", "-"));
                    resp.put("totalAmount", order.getTotalAmount());

                    // Thông tin khách hàng
                    if (order.getUser() != null) {
                        resp.put("customerName", order.getUser().getFullName());
                        resp.put("phone", order.getUser().getPhoneNumber());
                    }
                    resp.put("address", order.getDeliveryAddress());
                    // Danh sách món ăn
                    List<Map<String, Object>> items = order.getOrderDetails().stream().map(d -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("productName", d.getMenuItem() != null ? d.getMenuItem().getItemName() : "Unknown");
                        item.put("quantity", d.getQuantity());
                        item.put("price", d.getUnitPrice());
                        item.put("subtotal", d.getSubtotal());
                        return item;
                    }).collect(java.util.stream.Collectors.toList());
                    resp.put("items", items);
                    return ResponseEntity.ok(resp);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
