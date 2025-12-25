package com.dinerestaurant.app.data.remote.dto;

import java.math.BigDecimal;

public class ApplyPromotionResponse {

    private Integer orderId;
    private Integer promotionId;
    private String promotionTitle;
    private Integer discountPercent;
    private BigDecimal originalTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }
}
