package com.dine.adminweb.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AdminOrderDetailDto {

    private Integer orderId;
    private String orderStatus;
    private String orderStatusKey;
    private BigDecimal totalAmount;

    private String customerName;
    private String phone;
    private String address;

    private List<OrderItemDto> items;
}
