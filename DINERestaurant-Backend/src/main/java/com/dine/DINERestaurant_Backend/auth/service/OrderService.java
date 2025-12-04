package com.dine.DINERestaurant_Backend.service;

import com.dine.DINERestaurant_Backend.dto.OrderRequest;
import com.dine.DINERestaurant_Backend.entity.Order;
import com.dine.DINERestaurant_Backend.entity.OrderDetail;
import com.dine.DINERestaurant_Backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Đặt đơn tại bàn (Onsite)
     */
    @Transactional
    public Order createOnsiteOrder(OrderRequest request) {
        validateOnsiteOrder(request);

        Order order = buildOrder(request);
        order.setOrderType("Tại chỗ");
        order.setDeliveryFee(BigDecimal.ZERO);

        return saveOrder(order, request);
    }

    /**
     * Đặt đơn giao hàng (Delivery)
     */
    @Transactional
    public Order createDeliveryOrder(OrderRequest request) {
        validateDeliveryOrder(request);

        Order order = buildOrder(request);
        order.setOrderType("Giao hàng");

        // Tính phí giao hàng (có thể dựa vào khoảng cách)
        if (request.getDeliveryFee() == null) {
            order.setDeliveryFee(BigDecimal.valueOf(15000)); // Phí mặc định
        }

        return saveOrder(order, request);
    }

    /**
     * Đặt đơn mang về (Pickup)
     */
    @Transactional
    public Order createPickupOrder(OrderRequest request) {
        validatePickupOrder(request);

        Order order = buildOrder(request);
        order.setOrderType("Mang về");
        order.setDeliveryFee(BigDecimal.ZERO);

        return saveOrder(order, request);
    }

    /**
     * Build order object
     */
    private Order buildOrder(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTableId(request.getTableId());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotalAmount(request.getTotalAmount());
        order.setDeliveryFee(request.getDeliveryFee());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setOrderStatus("Đã đặt");
        order.setCreatedAt(LocalDateTime.now());

        return order;
    }

    /**
     * Save order with details
     */
    private Order saveOrder(Order order, OrderRequest request) {
        Order savedOrder = orderRepository.save(order);

        // Tạo order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        request.getItems().forEach(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(savedOrder.getOrderId());
            detail.setItemId(item.getItemId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderDetails.add(detail);
        });

        savedOrder.setOrderDetails(orderDetails);
        return orderRepository.save(savedOrder);
    }

    /**
     * Validation methods
     */
    private void validateOnsiteOrder(OrderRequest request) {
        if (request.getTableId() == null) {
            throw new RuntimeException("Table ID is required for onsite orders");
        }
    }

    private void validateDeliveryOrder(OrderRequest request) {
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().isEmpty()) {
            throw new RuntimeException("Delivery address is required");
        }
    }

    private void validatePickupOrder(OrderRequest request) {
        // Pickup không cần validate đặc biệt
    }

    /**
     * Lịch sử đơn hàng của user
     */
    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findUserOrdersDesc(userId);
    }

    /**
     * Chi tiết đơn hàng
     */
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    /**
     * Theo dõi trạng thái đơn hàng
     */
    public String getOrderStatus(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        return order != null ? order.getOrderStatus() : null;
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @Transactional
    public Order updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setOrderStatus(newStatus);
            return orderRepository.save(order);
        }
        return null;
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional
    public boolean cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && !order.getOrderStatus().equals("Hoàn thành")) {
            order.setOrderStatus("Đã hủy");
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    /**
     * Lấy đơn hàng theo trạng thái
     */
    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return orderRepository.findAll();
        }
        return orderRepository.findByOrderStatus(status);
    }

    /**
     * Tạo mã đơn hàng
     */
    public String generateOrderNumber(Integer orderId) {
        return "SP " + String.format("%07d", orderId);
    }
}