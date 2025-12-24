package com.dine.DINERestaurant_Backend.promotions.admin.service;

import com.dine.DINERestaurant_Backend.promotions.Entity.Promotion;
import com.dine.DINERestaurant_Backend.promotions.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PromotionAdminService {

    private final PromotionRepository promotionRepository;

    public PromotionAdminService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    // ADMIN: tạo khuyến mãi
    public Promotion create(Promotion promotion) {
        promotion.setId(null); // ép tạo mới
        return promotionRepository.save(promotion);
    }

    // ADMIN: cập nhật khuyến mãi
    public Promotion update(Integer id, Promotion data) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));

        promotion.setTitle(data.getTitle());
        promotion.setDescription(data.getDescription());
        promotion.setImage(data.getImage());
        promotion.setDiscountPercent(data.getDiscountPercent());
        promotion.setStartDate(data.getStartDate());
        promotion.setEndDate(data.getEndDate());
        promotion.setIsActive(data.getIsActive());

        return promotionRepository.save(promotion);
    }

    // ADMIN: bật / tắt khuyến mãi
    public Promotion changeStatus(Integer id, Boolean isActive) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));

        promotion.setIsActive(isActive);
        return promotionRepository.save(promotion);
    }

    // ADMIN: xem tất cả khuyến mãi
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    // ADMIN: xóa khuyến mãi
    public void delete(Integer id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi");
        }
        promotionRepository.deleteById(id);
    }
}
