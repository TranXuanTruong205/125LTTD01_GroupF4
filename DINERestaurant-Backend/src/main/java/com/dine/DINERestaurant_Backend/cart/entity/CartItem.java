package com.dine.DINERestaurant_Backend.cart.entity;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

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
    @JsonIgnore
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem menuItem;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "cart_item_options",
            joinColumns = @JoinColumn(name = "cart_item_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<ItemOption> options; // Chỉ cần giữ lại 1 đoạn này

    // Hàm này rất quan trọng để tính tổng tiền chính xác (Base + Toppings)
    public BigDecimal getLinePrice() {
        BigDecimal total = price; // Giá gốc món ăn
        if (options != null) {
            for (ItemOption opt : options) {
                if (opt.getExtraPrice() != null) {
                    total = total.add(opt.getExtraPrice());
                }
            }
        }
        return total.multiply(BigDecimal.valueOf(quantity));
    }
}