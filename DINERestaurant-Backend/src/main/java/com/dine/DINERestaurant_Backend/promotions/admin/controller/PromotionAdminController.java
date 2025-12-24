package com.dine.DINERestaurant_Backend.promotions.admin.controller;

import com.dine.DINERestaurant_Backend.promotions.Entity.Promotion;
import com.dine.DINERestaurant_Backend.promotions.admin.service.PromotionAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
public class PromotionAdminController {

    private final PromotionAdminService promotionAdminService;

    public PromotionAdminController(PromotionAdminService promotionAdminService) {
        this.promotionAdminService = promotionAdminService;
    }

    // POST /api/admin/promotions
    // Tạo khuyến mãi
    @PostMapping
    public Promotion create(@RequestBody Promotion promotion) {
        return promotionAdminService.create(promotion);
    }

    // PUT /api/admin/promotions/{id}
    // Cập nhật khuyến mãi
    @PutMapping("/{id}")
    public Promotion update(
            @PathVariable Integer id,
            @RequestBody Promotion promotion
    ) {
        return promotionAdminService.update(id, promotion);
    }

    // PUT /api/admin/promotions/{id}/status?isActive=true
    // Bật / tắt khuyến mãi
    @PutMapping("/{id}/status")
    public Promotion changeStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isActive
    ) {
        return promotionAdminService.changeStatus(id, isActive);
    }

    // GET /api/admin/promotions
    // Xem toàn bộ khuyến mãi
    @GetMapping
    public List<Promotion> getAll() {
        return promotionAdminService.getAll();
    }

    // DELETE /api/admin/promotions/{id}
    // Xóa khuyến mãi
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        promotionAdminService.delete(id);
    }
}
