package com.dine.DINERestaurant_Backend.cart.repository;
import com.dine.DINERestaurant_Backend.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCart_CartIdAndMenuItem_ItemId(Integer cartId, Integer itemId);
}