package com.dine.DINERestaurant_Backend.cart.entity;

import com.dine.DINERestaurant_Backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void calculateTotal() {
        this.totalAmount = cartItems.stream()
                .map(CartItem::getLinePrice) // Sử dụng line price thay vì chỉ price * quantity
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

