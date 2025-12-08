package com.dine.DINERestaurant_Backend.order.service;

import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.entity.CartItem;
import com.dine.DINERestaurant_Backend.cart.service.CartService;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.entity.OrderDetail;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
import com.dine.DINERestaurant_Backend.order.repository.UserAddressRepository;
import com.dine.DINERestaurant_Backend.user.entity.UserAddress;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;
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
    public boolean cancelOrder(Integer orderId, Integer userId) {
        Optional<Order> opt = orderRepository.findById(orderId);

        if (opt.isEmpty()) return false;

        Order order = opt.get();

        // Kiểm tra quyền sở hữu đơn hàng
        if (!order.getUserId().equals(userId)) {
            return false; // user khác → không được hủy
        }

        // Nếu trạng thái không thể hủy
        if (order.getOrderStatus().equals("Hoàn thành")) {
            return false;
        }

        order.setOrderStatus("Đã hủy");
        orderRepository.save(order);
        return true;
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
    // ====================== CHECKOUT TỪ GIỎ HÀNG ======================
    @Transactional
    public Order createOrderFromCart(Integer userId, String orderType,
                                     Integer tableId, Integer addressId,
                                     String paymentMethod, String note) {

        // Lấy giỏ hàng của user
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống! Vui lòng thêm món trước khi thanh toán.");
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderType(orderType);
        order.setTableId(tableId);
        // Nếu là giao hàng → lấy địa chỉ text từ user_addresses
        if ("Giao hàng".equals(orderType) && addressId != null) {
            UserAddress addr = userAddressRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));
            order.setDeliveryAddress(addr.getAddressText());
        } else {
            order.setDeliveryAddress(null);
        }
        order.setPaymentMethod(paymentMethod);
        order.setNote(note);
        order.setOrderStatus("Đã đặt");
        order.setCreatedAt(LocalDateTime.now());

        // Tính tổng tiền + tạo chi tiết đơn hàng từ giỏ
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            MenuItem menuItem = cartItem.getMenuItem();
            if (menuItem == null) continue;

            BigDecimal unitPrice = cartItem.getPrice();
            Integer quantity = cartItem.getQuantity();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderDetail detail = new OrderDetail();
            detail.setItemId(menuItem.getItemId());
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice);
            detail.setSubtotal(subtotal);
            orderDetails.add(detail);
        }

        // Phí ship
        if ("Giao hàng".equals(orderType)) {
            order.setDeliveryFee(addressId != null ? BigDecimal.valueOf(20000) : BigDecimal.valueOf(15000));
        } else {
            order.setDeliveryFee(BigDecimal.ZERO);
        }

        order.setTotalAmount(totalAmount.add(order.getDeliveryFee()));

        // Save đơn hàng trước để có ID
        Order savedOrder = orderRepository.save(order);

        // Gán orderId cho các detail
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(savedOrder.getOrderId());
        }
        savedOrder.setOrderDetails(orderDetails);

        // Lưu lại lần cuối
        return orderRepository.save(savedOrder);
    }
}