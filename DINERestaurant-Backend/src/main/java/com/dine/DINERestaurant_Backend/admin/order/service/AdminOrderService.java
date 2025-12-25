package com.dine.DINERestaurant_Backend.admin.order.service;

import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Lấy danh sách toàn bộ đơn hàng của nhà hàng
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public List<Order> getOngoingOrders() {
        return orderRepository.findByOrderStatusInOrderByCreatedAtDesc(
                List.of("Đã đặt", "Đã nhận đơn", "Đang làm món", "Đang giao")
        );
    }
    // Đơn đã kết thúc: Hoàn thành, Đã hủy
    public List<Order> getOrderHistory() {
        return orderRepository.findByOrderStatusInOrderByCreatedAtDesc(
                List.of("Hoàn thành", "Đã hủy")
        );
    }
    /**
     * Cập nhật trạng thái đơn hàng (Dành cho Admin)
     */
    @Transactional
    public Order changeStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setOrderStatus(status);
            return orderRepository.save(order);
        }
        return null; // Không tìm thấy đơn hàng
    }
}