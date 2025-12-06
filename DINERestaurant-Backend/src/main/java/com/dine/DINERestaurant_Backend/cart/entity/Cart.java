package com.dine.DINERestaurant_Backend.cart.entity;
import com.dine.DINERestaurant_Backend.auth.entity.User;
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
    // Mỗi user có 1 giỏ hàng
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    // Một giỏ hàng có nhiều món
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
    // Tổng tiền tạm tính (có thể tính toán động)
    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void calculateTotal() {
        this.totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}