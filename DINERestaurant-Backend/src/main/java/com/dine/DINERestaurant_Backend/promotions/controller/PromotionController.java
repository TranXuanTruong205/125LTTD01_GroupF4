package com.dine.DINERestaurant_Backend.promotions.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionRequest;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.DTO.PromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.service.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    private final JwtUtil jwtUtil;

    public PromotionController(PromotionService promotionService, JwtUtil jwtUtil) {
        this.promotionService = promotionService;
        this.jwtUtil = jwtUtil;
    }

    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7);
        return Long.valueOf(jwtUtil.extractUserId(token));
    }

    // GET /api/promotions
    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getPromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    // POST /api/promotions/apply
    @PostMapping("/apply")
    public ResponseEntity<ApplyPromotionResponse> applyPromotion(
            @RequestHeader("Authorization") String token,
            @RequestBody ApplyPromotionRequest request
    ) {
        Long userId = getCurrentUserId(token);
        return ResponseEntity.ok(
                promotionService.applyPromotion(userId, request)
        );
    }
}
