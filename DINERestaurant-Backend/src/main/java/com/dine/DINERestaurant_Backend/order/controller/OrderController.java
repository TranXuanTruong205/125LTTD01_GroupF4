package com.dine.DINERestaurant_Backend.order.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.cart.service.CartService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.service.OrderService;
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
    @Autowired private JwtUtil jwtUtil;
    @Autowired private OrderService orderService;
    @Autowired private CartService cartService; // ← THÊM DÒNG NÀY!!!

    private Integer extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ");
        }
        String token = authHeader.substring(7);
        String userIdStr = jwtUtil.extractUserId(token);
        return Integer.parseInt(userIdStr);
    }

    // ==================== API MỚI: CHECKOUT TỪ GIỎ HÀNG ====================
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkoutFromCart(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();
        try {
            Integer userId = extractUserIdFromToken(authHeader);

            // Lấy danh sách ID các món người dùng đã tích chọn từ Android gửi lên
            List<Integer> cartItemIds = (List<Integer>) request.get("cartItemIds");

            String orderType = (String) request.get("orderType");
            Integer tableId = request.get("tableId") != null ? (Integer) request.get("tableId") : null;
            Integer addressId = request.get("addressId") != null ? (Integer) request.get("addressId") : null;
            String paymentMethod = (String) request.get("paymentMethod");
            String note = request.get("note") != null ? (String) request.get("note") : null;

            // Gọi Service với tham số cartItemIds mới
            Order order = orderService.createOrderFromCart(userId, orderType, tableId, addressId, paymentMethod, note, cartItemIds);

            response.put("success", true);
            response.put("message", "Đặt hàng thành công!");
            response.put("data", order);
            response.put("orderNumber", orderService.generateOrderNumber(order.getOrderId()));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
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

    // ==================== CÁC API KHÁC (trả Entity trong data) ====================
    private Integer getCurrentUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ");
        }
        String token = authHeader.substring(7); // bỏ "Bearer "

        // Giải mã token (dùng cách bạn đang có trong dự án)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("${jwt.secret}") // ← thay bằng key thật của bạn (tìm trong JwtTokenProvider hoặc application.yml)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject(); // vì sub = userId trong token của bạn
            return Integer.parseInt(userIdStr);
        } catch (Exception e) {
            throw new RuntimeException("Token hết hạn hoặc không hợp lệ");
        }
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyOrders(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy token từ header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(401).body(response);
            }

            String token = authHeader.substring(7); // bỏ "Bearer "
            String userIdStr = jwtUtil.extractUserId(token); // ← CHÍNH LÀ HÀM NÀY!!!
            Integer userId = Integer.parseInt(userIdStr);

            List<Order> orders = orderService.getUserOrders(userId);
            response.put("success", true);
            response.put("data", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token hết hạn hoặc không hợp lệ: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
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
            } }
        catch(Exception e){
            response.put("success", false);
            response.put("message", "Token hết hạn hoặc không hợp lệ: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("{id}/status")
    public ResponseEntity<Map<String, Object>> getOrderStatus(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
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
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token hết hạn hoặc không hợp lệ: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(401).body(response);
            }

            String token = authHeader.substring(7);
            String userIdStr = jwtUtil.extractUserId(token);
            Integer userId = Integer.parseInt(userIdStr);

            // Hủy đơn hàng theo order_id và user_id
            boolean cancelled = orderService
                    .cancelOrder(id, userId);

            if (cancelled) {
                response.put("success", true);
                response.put("message", "Đơn hàng đã được hủy thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Bạn không có quyền hủy đơn hàng này hoặc đơn đã hoàn thành");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }


    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestHeader("Authorization") String authHeader,@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy token từ header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(401).body(response);
            }
            String token = authHeader.substring(7); // bỏ "Bearer "
            String userIdStr = jwtUtil.extractUserId(token); // ← CHÍNH LÀ HÀM NÀY!!!
            Integer userId = Integer.parseInt(userIdStr);
            String newStatus = body.get("status");

            Order order = orderService.updateOrderStatus(userId, newStatus);
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
        catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token hết hạn hoặc không hợp lệ: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
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