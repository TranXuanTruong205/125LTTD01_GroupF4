package com.dine.adminweb.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    private Integer orderId;
    private String orderStatus;
    private String orderStatusKey;

    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    // Customer
    private Integer userId;
    private String customerName;
    private String phone;
    private String address;

    // Items
    private List<OrderItemDto> items;
}
