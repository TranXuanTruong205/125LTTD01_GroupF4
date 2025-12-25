package com.dine.DINERestaurant_Backend.review.service;


import com.dine.DINERestaurant_Backend.review.DTO.CreateReviewRequest;
import com.dine.DINERestaurant_Backend.review.DTO.ReviewResponse;
import com.dine.DINERestaurant_Backend.review.DTO.UpdateReviewRequest;
import com.dine.DINERestaurant_Backend.review.entity.Review;
import com.dine.DINERestaurant_Backend.review.respository.ReviewRepository;
import com.dine.DINERestaurant_Backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final UserService userService;
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserService userService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }


    // Viết review
    public ReviewResponse createReview(Long currentUserId, CreateReviewRequest request) {
        // Validate rating
        if (request.getRating() == null ||
                request.getRating() < 1 ||
                request.getRating() > 5) {
            throw new IllegalArgumentException("Rating phải từ 1 đến 5");
        }

        // Tạo entity
        Review review = new Review();
        review.setUserId(currentUserId);
        review.setItemId(request.getItemId());
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        // Check user đã từng mua món này chưa
        int purchasedCount = reviewRepository.countUserPurchasedItem(currentUserId, request.getItemId());
        boolean hasPurchased = purchasedCount > 0;
        review.setVerifiedPurchase(hasPurchased);

        // Nếu muốn CHẶN luôn người chưa mua thì m có thể:
        // if (!hasPurchased) {
        //     throw new IllegalStateException("Bạn chưa mua món này nên không thể đánh giá.");
        // }

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(Long currentUserId,
                                       Long reviewId,
                                       UpdateReviewRequest request) {

        // 1. Tìm review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy review"));

        // 2. Kiểm tra quyền: chỉ chủ review mới được sửa
        if (!review.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thể sửa review của người khác"
            );
        }

        // 3. Nếu có gửi rating mới thì validate + set
        if (request.getRating() != null) {
            int newRating = request.getRating();
            if (newRating < 1 || newRating > 5) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Rating phải từ 1 đến 5"
                );
            }
            review.setRating(newRating);
        }

        // 4. Nếu có gửi comment mới thì set
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        // 5. Lưu lại
        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }
    // Review theo món
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByItem(Long itemId) {
        return reviewRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Review của user (current user)
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setItemId(review.getItemId());
        dto.setOrderId(review.getOrderId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setVerifiedPurchase(Boolean.TRUE.equals(review.getVerifiedPurchase()));
        dto.setCreatedAt(review.getCreatedAt());

        userService.getUserById(String.valueOf(review.getUserId()))
                .ifPresent(user -> dto.setUserName(user.getFullName()));

        return dto;
    }
}
