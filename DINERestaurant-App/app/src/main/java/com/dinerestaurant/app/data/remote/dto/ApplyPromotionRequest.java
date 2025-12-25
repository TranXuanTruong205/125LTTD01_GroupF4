package com.dinerestaurant.app.data.remote.dto;

public class ApplyPromotionRequest {
    private Integer cartId;
    private Integer promotionId;

    public ApplyPromotionRequest(Integer cartId, Integer promotionId) {
        this.cartId = cartId;
        this.promotionId = promotionId;
    }

    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }

    public Integer getPromotionId() { return promotionId; }
    public void setPromotionId(Integer promotionId) { this.promotionId = promotionId; }
}


