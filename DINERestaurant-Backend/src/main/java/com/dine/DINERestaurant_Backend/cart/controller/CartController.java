package com.dine.DINERestaurant_Backend.cart.controller;

import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil jwtUtil;
    private Integer getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ");
        }
        String token = authHeader.substring(7);
        String userIdStr = jwtUtil.extractUserId(token);
        return Integer.parseInt(userIdStr);
    }
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody Map<String, Object> payload,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            Integer itemId = (Integer) payload.get("menuItemId");
            Integer quantity = (Integer) payload.get("quantity");
            List<Integer> optionIds = (List<Integer>) payload.get("optionIds");

            // Gọi sang Service để xử lý logic lưu Database
            return ResponseEntity.ok(cartService.addToCart(userId, itemId, quantity, optionIds));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/update")
    public ResponseEntity<Cart> updateQuantity(@RequestBody Map<String, Object> payload,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            Integer cartItemId = (Integer) payload.get("cartItemId");
            Integer quantity = (Integer) payload.get("quantity");
            return ResponseEntity.ok(cartService.updateQuantity(userId, cartItemId, quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Integer cartItemId,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            cartService.removeCartItem(userId, cartItemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = getUserIdFromToken(authHeader);
            cartService.clearCart(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}