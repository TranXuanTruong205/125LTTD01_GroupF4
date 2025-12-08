package com.dine.DINERestaurant_Backend.cart.service;

import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.entity.CartItem;
import com.dine.DINERestaurant_Backend.cart.repository.CartRepository;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuItemRepository menuItemRepository;

    // Lấy giỏ hàng theo User ID
    public Cart getCartByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addToCart(Integer userId, Integer menuItemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(ci -> ci.getMenuItem().getItemId().equals(menuItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMenuItem(item);
            newItem.setQuantity(quantity);
            newItem.setPrice(item.getPrice());
            cart.getCartItems().add(newItem);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Integer userId, Integer cartItemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cart.getCartItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        cart.calculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItems().clear();
        cart.setTotalAmount(java.math.BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeCartItem(Integer userId, Integer cartItemId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItems().removeIf(item -> item.getCartItemId().equals(cartItemId));
        cart.calculateTotal();
        cartRepository.save(cart);
    }
}
