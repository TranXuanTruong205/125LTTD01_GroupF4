package com.dine.adminweb.dto;

import lombok.Data;
import java.math.BigDecimal;
@Data
public class DashboardStatsDto {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private java.util.List<java.util.Map<String, Object>> recentOrders;
    private java.util.List<java.util.Map<String, Object>> recentCustomers;
}