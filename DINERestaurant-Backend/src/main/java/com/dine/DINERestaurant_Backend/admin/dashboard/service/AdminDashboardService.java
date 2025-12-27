package com.dine.DINERestaurant_Backend.admin.dashboard.service;

import com.dine.DINERestaurant_Backend.admin.dashboard.dto.DashboardStats;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class AdminDashboardService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        // 1. Thống kê số lượng
        stats.setTotalOrders(orderRepository.count());
        stats.setTotalCustomers(userRepository.count());

        // 2. Tính doanh thu (SỬA LẠI ĐOẠN NÀY)
        BigDecimal revenue = orderRepository.sumCompletedOrdersRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        // 3. Lấy danh sách mới nhất
        stats.setRecentOrders(
                orderRepository.findAll(
                        PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending())
                ).getContent()
        );

        stats.setRecentCustomers(
                userRepository.findAll(
                        PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending())
                ).getContent()
        );

        return stats;
    }
}