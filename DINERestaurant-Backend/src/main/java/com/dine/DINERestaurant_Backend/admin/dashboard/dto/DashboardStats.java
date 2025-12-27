package com.dine.DINERestaurant_Backend.admin.dashboard.dto;
import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.user.entity.User;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardStats {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private List<Order> recentOrders;
    private List<User> recentCustomers;
}