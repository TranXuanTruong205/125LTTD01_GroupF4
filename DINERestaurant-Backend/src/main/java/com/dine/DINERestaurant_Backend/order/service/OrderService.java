package com.dine.DINERestaurant_Backend.order.service;

import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.entity.CartItem;
import com.dine.DINERestaurant_Backend.cart.service.CartService;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
import com.dine.DINERestaurant_Backend.order.entity.Order;
import com.dine.DINERestaurant_Backend.order.entity.OrderDetail;
import com.dine.DINERestaurant_Backend.order.repository.OrderRepository;
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
    private com.dine.DINERestaurant_Backend.user.repository.UserAddressRepository userAddressRepository;
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
    public Order createOrderFromCart(
            Integer userId,
            String orderType,
            Integer tableId,
            Integer addressId,
            String paymentMethod,
            String note,
            List<Integer> cartItemIds
    ) {
        // 1) Validate input
        if (userId == null) throw new RuntimeException("Thiếu userId!");
        if (cartItemIds == null || cartItemIds.isEmpty())
            throw new RuntimeException("Chưa chọn món nào để thanh toán!");

        if (orderType == null || orderType.isBlank())
            throw new RuntimeException("Thiếu orderType!");

        // Nếu là ăn tại bàn mà không có tableId (tuỳ bạn có dùng loại này không)
        if (!"Giao hàng".equals(orderType) && tableId == null) {
            // Nếu bạn có các loại khác (Mang về) thì chỉnh điều kiện theo hệ thống của bạn
            // Ở đây mình để an toàn: không giao hàng thì cần tableId
            // Nếu bạn không cần tableId cho "Mang về" thì bỏ check này.
            // throw new RuntimeException("Thiếu tableId cho đơn không giao hàng!");
        }

        // 2) Lấy giỏ hàng (cartService đã đảm nhiệm tạo cart nếu chưa có)
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty())
            throw new RuntimeException("Giỏ hàng trống! Vui lòng thêm món trước khi thanh toán.");

        // 3) Lọc ra đúng các món được chọn để checkout
        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> item != null
                        && item.getCartItemId() != null
                        && cartItemIds.contains(item.getCartItemId()))
                .toList();

        if (selectedItems.isEmpty())
            throw new RuntimeException("Chưa chọn món nào để thanh toán!");

        // 4) Tạo Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderType(orderType);
        order.setTableId(tableId);

        // Nếu là giao hàng → lấy địa chỉ text từ user_addresses
        if ("Giao hàng".equals(orderType)) {
            if (addressId != null) {
                UserAddress addr = userAddressRepository.findById(addressId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));
                order.setDeliveryAddress(addr.getAddressText());
            } else {
                // tuỳ nghiệp vụ: cho phép null thì để null, không cho thì throw
                order.setDeliveryAddress(null);
                // throw new RuntimeException("Thiếu addressId cho đơn giao hàng!");
            }
        } else {
            order.setDeliveryAddress(null);
        }

        order.setPaymentMethod(paymentMethod);
        order.setNote(note);
        order.setOrderStatus("Đã đặt");
        order.setCreatedAt(LocalDateTime.now());

        // 5) Tính tiền + tạo OrderDetail từ selectedItems (ĐÃ FIX GIÁ TOPPING)
        BigDecimal itemsTotal = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : selectedItems) {
            MenuItem menuItem = cartItem.getMenuItem();
            if (menuItem == null) continue;

            // Lấy TỔNG GIÁ của dòng này (Đã bao gồm Topping + số lượng)
            BigDecimal lineTotal = cartItem.getLinePrice();
            Integer quantity = cartItem.getQuantity();
            if (quantity == null || quantity <= 0) continue;
            if (lineTotal == null) lineTotal = BigDecimal.ZERO;
            // Tính lại đơn giá thực tế (đã gồm Topping) để hiển thị trên hóa đơn
            BigDecimal unitPriceWithToppings = lineTotal.divide(BigDecimal.valueOf(quantity), 2, java.math.RoundingMode.HALF_UP);

            itemsTotal = itemsTotal.add(lineTotal);

            OrderDetail detail = new OrderDetail();
            detail.setItemId(menuItem.getItemId());
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPriceWithToppings); // Đơn giá có tính cả Topping
            detail.setSubtotal(lineTotal);

            orderDetails.add(detail);
        }

        if (orderDetails.isEmpty())
            throw new RuntimeException("Danh sách món thanh toán không hợp lệ!");

        // 6) Phí ship
        if ("Giao hàng".equals(orderType)) {
            // bạn có thể tính theo khoảng cách/địa chỉ; tạm giữ như code cũ của bạn
            order.setDeliveryFee(addressId != null ? BigDecimal.valueOf(20000) : BigDecimal.valueOf(15000));
        } else {
            order.setDeliveryFee(BigDecimal.ZERO);
        }

        // Tổng tiền cuối (items + ship)
        order.setTotalAmount(itemsTotal.add(order.getDeliveryFee()));

        // 7) Save order trước để có orderId
        Order savedOrder = orderRepository.save(order);

        // Gán orderId cho detail
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(savedOrder.getOrderId());
        }
        savedOrder.setOrderDetails(orderDetails);

        // Save lại lần 2 để lưu detail (giống pattern bạn đang dùng)
        savedOrder = orderRepository.save(savedOrder);

        // 8) Sau khi tạo đơn THÀNH CÔNG → mới xóa các món đã checkout khỏi cart
        cart.getCartItems().removeIf(item -> item != null
                && item.getCartItemId() != null
                && cartItemIds.contains(item.getCartItemId()));
        cart.calculateTotal();
        cartService.saveCart(cart);
        return savedOrder;
    }
}