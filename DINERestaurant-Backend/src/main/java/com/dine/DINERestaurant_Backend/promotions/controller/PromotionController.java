package com.dine.DINERestaurant_Backend.promotions.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionRequest;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.DTO.PromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    private JwtUtil jwtUtil;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7); // "Bearer xxx"
        String userId = jwtUtil.extractUserId(token);
        return Long.valueOf(userId);
    }

    // ===========================
    // GET /api/promotions
    // Danh sách khuyến mãi đang còn hiệu lực
    // ===========================
    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getPromotions() {
        List<PromotionResponse> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    // ===========================
    // POST /api/promotions/apply
    // Áp dụng KM vào đơn
    // ===========================
    @PostMapping("/apply")
    public ResponseEntity<ApplyPromotionResponse> applyPromotion(
            @RequestHeader("Authorization") String token,
            @RequestBody ApplyPromotionRequest request
    ) {
        Long currentUserId = getCurrentUserId(token);
        ApplyPromotionResponse response = promotionService.applyPromotion(currentUserId, request);
        return ResponseEntity.ok(response);
    }
}
