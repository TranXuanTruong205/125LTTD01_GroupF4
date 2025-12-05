package com.dine.DINERestaurant_Backend.review.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.review.DTO.CreateReviewRequest;
import com.dine.DINERestaurant_Backend.review.DTO.ReviewResponse;
import com.dine.DINERestaurant_Backend.review.DTO.UpdateReviewRequest;
import com.dine.DINERestaurant_Backend.review.service.ReviewService;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import com.dine.DINERestaurant_Backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    private String getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return userId;
    }
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ===========================
    // POST /api/reviews  (Viết review)
    // ===========================
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestHeader("Authorization") String token,
            @RequestBody CreateReviewRequest request
    ) {
        Long currentUserId = Long.valueOf(getCurrentUserId(token)); // Lấy từ JWT
        ReviewResponse response = reviewService.createReview(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest request
    ) {
        Long currentUserId = Long.valueOf(getCurrentUserId(token));
        ReviewResponse response = reviewService.updateReview(currentUserId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    // ===========================
    // GET /api/reviews/item/{itemId} (Review theo món)
    // ===========================
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByItem(
            @PathVariable Long itemId
    ) {
        List<ReviewResponse> reviews = reviewService.getReviewsByItem(itemId);
        return ResponseEntity.ok(reviews);
    }

    // ===========================
    // GET /api/reviews/my (Review của user hiện tại)
    // ===========================
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@RequestHeader("Authorization") String token) {

        Long currentUserId = Long.valueOf(getCurrentUserId(token));
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(currentUserId);
        return ResponseEntity.ok(reviews);
    }

    // ===========================
    // Hàm này m COPY logic sẵn có trong UserController
    // (lấy userId từ JWT / SecurityContext)
    // ===========================

}
