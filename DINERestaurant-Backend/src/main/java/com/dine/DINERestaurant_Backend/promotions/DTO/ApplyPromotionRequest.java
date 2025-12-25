package com.dine.DINERestaurant_Backend.promotions.DTO;

public class ApplyPromotionRequest {

    private Integer cartId;
    private Integer promotionId;

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }
}