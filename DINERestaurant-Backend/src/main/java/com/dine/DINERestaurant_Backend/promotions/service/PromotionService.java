package com.dine.DINERestaurant_Backend.promotions.service;

import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    public PromotionService(PromotionRepository promotionRepository,
                            OrderRepository orderRepository) {
        this.promotionRepository = promotionRepository;
        this.orderRepository = orderRepository;
    }

    // Lấy danh sách khuyến mãi đang còn hiệu lực
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActivePromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findActivePromotions(today);
        return promotions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Áp dụng KM vào đơn
    public ApplyPromotionResponse applyPromotion(Long currentUserId,
                                                 ApplyPromotionRequest request) {
        // 1. Lấy order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        // 2. Chỉ cho CHỦ đơn được áp dụng
        if (!order.getUserId().equals(currentUserId.intValue())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không thể áp dụng khuyến mãi cho đơn của người khác"
            );
        }

        // 3. Lấy promotion
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));

        // 4. Validate promotion còn hiệu lực
        LocalDate today = LocalDate.now();
        if (promotion.getIsActive() == null || !promotion.getIsActive()
                || today.isBefore(promotion.getStartDate())
                || today.isAfter(promotion.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Khuyến mãi này hiện không còn hiệu lực"
            );
        }

        Integer percent = promotion.getDiscountPercent();
        if (percent == null || percent <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Khuyến mãi này không có % giảm giá để áp dụng"
            );
        }

        // 5. Tính tiền
        BigDecimal originalTotal = order.getTotalAmount();
        if (originalTotal == null) {
            originalTotal = BigDecimal.ZERO;
        }

        BigDecimal discountPercent = BigDecimal.valueOf(percent)
                .divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = originalTotal.multiply(discountPercent);
        BigDecimal finalTotal = originalTotal.subtract(discountAmount);

        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        // 6. Cập nhật vào đơn
        order.setTotalAmount(finalTotal);

        String note = order.getNote();
        String promoNote = "[KM] " + promotion.getTitle() + " (-" + percent + "%)";
        if (note == null || note.isBlank()) {
            order.setNote(promoNote);
        } else {
            order.setNote(note + " | " + promoNote);
        }

        orderRepository.save(order);

        // 7. Trả về response
        ApplyPromotionResponse response = new ApplyPromotionResponse();
        response.setOrderId(order.getOrderId());
        response.setPromotionId(promotion.getId());
        response.setPromotionTitle(promotion.getTitle());
        response.setDiscountPercent(percent);
        response.setOriginalTotal(originalTotal);
        response.setDiscountAmount(discountAmount);
        response.setFinalTotal(finalTotal);

        return response;
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        PromotionResponse dto = new PromotionResponse();
        dto.setId(promotion.getId());
        dto.setTitle(promotion.getTitle());
        dto.setDescription(promotion.getDescription());
        dto.setImage(promotion.getImage());
        dto.setDiscountPercent(promotion.getDiscountPercent());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setIsActive(promotion.getIsActive());
        return dto;
    }
}
