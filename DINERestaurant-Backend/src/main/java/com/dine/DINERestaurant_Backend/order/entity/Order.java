package com.dine.DINERestaurant_Backend.order.entity;

import com.dine.DINERestaurant_Backend.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "order_type", nullable = false, length = 20)
    private String orderType;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "delivery_address", columnDefinition = "NVARCHAR(MAX)")
    private String deliveryAddress;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod = "Tiền mặt";

    @Column(name = "order_status", length = 20)
    private String orderStatus = "Đã đặt";

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("com/dine/DINERestaurant_Backend/order")  // Ngăn không cho serialize field "order" trong OrderDetail
    private List<OrderDetail> orderDetails;
}