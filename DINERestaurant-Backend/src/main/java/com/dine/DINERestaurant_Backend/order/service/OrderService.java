package com.dine.DINERestaurant_Backend.order.service;

import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.entity.OrderDetail;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Hàm chung tạo đơn hàng - nhận Map từ Controller
    @Transactional
    public Order createOrder(Map<String, Object> request, String orderType) {
        Order order = new Order();
        order.setUserId((Integer) request.get("userId"));
        order.setTableId(request.get("tableId") != null ? (Integer) request.get("tableId") : null);
        order.setDeliveryAddress((String) request.get("deliveryAddress"));
        order.setPaymentMethod((String) request.get("paymentMethod"));
        order.setNote((String) request.get("note"));
        order.setOrderStatus("Đã đặt");
        order.setOrderType(orderType);
        order.setCreatedAt(LocalDateTime.now());

        // Tính tổng tiền từ items
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Integer itemId = (Integer) item.get("itemId");
            Integer quantity = (Integer) item.get("quantity");
            BigDecimal unitPrice = new BigDecimal(item.get("unitPrice").toString());

            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderDetail detail = new OrderDetail();
            detail.setItemId(itemId);
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice);
            detail.setSubtotal(subtotal);
            orderDetails.add(detail);
        }

        order.setTotalAmount(totalAmount);

        // Xử lý phí ship
        if ("Giao hàng".equals(orderType)) {
            order.setDeliveryFee(request.get("deliveryFee") != null ?
                    new BigDecimal(request.get("deliveryFee").toString()) : BigDecimal.valueOf(15000));
        } else {
            order.setDeliveryFee(BigDecimal.ZERO);
        }

        // Save order trước để có ID
        Order savedOrder = orderRepository.save(order);

        // Gán orderId cho các detail
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(savedOrder.getOrderId());
        }
        savedOrder.setOrderDetails(orderDetails);

        return orderRepository.save(savedOrder); // save lại lần 2 có detail
    }

    // Các hàm còn lại giữ nguyên (không cần thay đổi)
    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findUserOrdersDesc(userId);
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public String getOrderStatus(Integer orderId) {
        Order order = getOrderById(orderId);
        return order != null ? order.getOrderStatus() : null;
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, String newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setOrderStatus(newStatus);
            return orderRepository.save(order);
        }
        return null;
    }

    @Transactional
    public boolean cancelOrder(Integer orderId) {
        Order order = getOrderById(orderId);
        if (order != null && !"Hoàn thành".equals(order.getOrderStatus()) && !"Đã hủy".equals(order.getOrderStatus())) {
            order.setOrderStatus("Đã hủy");
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return orderRepository.findAll();
        }
        return orderRepository.findByOrderStatus(status);
    }

    public String generateOrderNumber(Integer orderId) {
        return "SP" + String.format("%07d", orderId);
    }
}