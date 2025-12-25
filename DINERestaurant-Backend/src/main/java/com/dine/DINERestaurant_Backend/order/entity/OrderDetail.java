package com.dine.DINERestaurant_Backend.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // OrderDetail.java – Chỉnh ngay dòng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @JsonIgnore  // Thêm dòng này
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private com.dine.DINERestaurant_Backend.menu.entity.MenuItem menuItem;
}