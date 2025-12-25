package com.dine.DINERestaurant_Backend.admin.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminOrderItemDto {

    private Integer itemId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
