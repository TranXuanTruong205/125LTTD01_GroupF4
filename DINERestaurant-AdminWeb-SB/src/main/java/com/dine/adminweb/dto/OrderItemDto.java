package com.dine.adminweb.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private Integer itemId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
