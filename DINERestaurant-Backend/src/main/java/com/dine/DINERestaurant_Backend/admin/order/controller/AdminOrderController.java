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

    /**
     * API: Lấy danh sách toàn bộ đơn hàng
     * Endpoint: GET /api/admin/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> listAll() {
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        return ResponseEntity.ok(adminOrderService.getOngoingOrders());
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> getHistory() {
        return ResponseEntity.ok(adminOrderService.getOrderHistory());
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        Order updated = adminOrderService.changeStatus(id, newStatus);

        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}