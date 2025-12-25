package com.dine.DINERestaurant_Backend.promotions.service;

import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.repository.CartRepository;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionRequest;
import com.dine.DINERestaurant_Backend.promotions.DTO.ApplyPromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.DTO.PromotionResponse;
import com.dine.DINERestaurant_Backend.promotions.Entity.Promotion;
import com.dine.DINERestaurant_Backend.promotions.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final CartRepository cartRepository;

    public PromotionService(PromotionRepository promotionRepository,
                            CartRepository cartRepository) {
        this.promotionRepository = promotionRepository;
        this.cartRepository = cartRepository;
    }

    // GET promotions
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // APPLY promotion
    public ApplyPromotionResponse applyPromotion(Long userId,
                                                 ApplyPromotionRequest request) {

        if (request.getCartId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "cartId không được null");
        }

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cart"));

        if (!cart.getUser().getUserId().equals(userId.intValue())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thể áp dụng khuyến mãi cho cart của người khác");
        }

        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));

        LocalDate today = LocalDate.now();
        if (!Boolean.TRUE.equals(promotion.getIsActive())
                || today.isBefore(promotion.getStartDate())
                || today.isAfter(promotion.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Khuyến mãi không còn hiệu lực");
        }

        BigDecimal originalTotal = cart.getTotalAmount();
        if (originalTotal == null) originalTotal = BigDecimal.ZERO;

        BigDecimal discountAmount = originalTotal
                .multiply(BigDecimal.valueOf(promotion.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100));

        BigDecimal finalTotal = originalTotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        ApplyPromotionResponse response = new ApplyPromotionResponse();
        response.setCartId(cart.getCartId());
        response.setPromotionId(promotion.getId());
        response.setPromotionTitle(promotion.getTitle());
        response.setDiscountPercent(promotion.getDiscountPercent());
        response.setOriginalTotal(originalTotal);
        response.setDiscountAmount(discountAmount);
        response.setFinalTotal(finalTotal);

        return response;
    }

    private PromotionResponse mapToResponse(Promotion p) {
        PromotionResponse dto = new PromotionResponse();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setImage(p.getImage());
        dto.setDiscountPercent(p.getDiscountPercent());
        dto.setStartDate(p.getStartDate());
        dto.setEndDate(p.getEndDate());
        dto.setIsActive(p.getIsActive());
        return dto;
    }
}
