package com.dine.DINERestaurant_Backend.promotions.DTO;

public class ApplyPromotionRequest {

    // CHANGED: dùng Integer cho khớp Order.orderId & Promotion.id
    private Integer orderId;
    private Integer promotionId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }
}
