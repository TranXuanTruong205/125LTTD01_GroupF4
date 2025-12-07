package com.dine.DINERestaurant_Backend.cart.entity;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore // Tránh vòng lặp vô hạn khi serialize JSON
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem menuItem;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "price")
    private BigDecimal price; // Lưu giá tại thời điểm thêm vào giỏ
}