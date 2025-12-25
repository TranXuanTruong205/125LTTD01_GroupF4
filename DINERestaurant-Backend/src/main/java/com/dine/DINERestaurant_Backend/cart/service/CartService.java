package com.dine.DINERestaurant_Backend.cart.service;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.entity.CartItem;
import com.dine.DINERestaurant_Backend.cart.repository.CartRepository;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
import com.dine.DINERestaurant_Backend.menu.repository.ItemOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private ItemOptionRepository itemOptionRepository;
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
    public Cart addToCart(Integer userId, Integer menuItemId, Integer quantity, List<Integer> optionIds) {
        Cart cart = getCartByUserId(userId);
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        // 1. Lấy danh sách ItemOption thực tế từ DB dựa trên optionIds gửi lên
        List<ItemOption> selectedOptions = itemOptionRepository.findAllById(optionIds);
        // 2. Tìm xem trong giỏ hàng đã có món "y hệt" (cùng món + cùng option) chưa
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(ci -> ci.getMenuItem().getItemId().equals(menuItemId))
                .filter(ci -> isSameOptions(ci.getOptions(), selectedOptions)) // Kiểm tra trùng Option
                .findFirst();
        if (existingItem.isPresent()) {
            // Nếu trùng hoàn toàn -> Chỉ tăng số lượng
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Nếu khác Option -> Tạo dòng mới trong giỏ hàng
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMenuItem(item);
            newItem.setQuantity(quantity);
            newItem.setOptions(selectedOptions); // Gán danh sách Option mới
            newItem.setPrice(item.getPrice());
            cart.getCartItems().add(newItem);
        }
        cart.calculateTotal();
        return cartRepository.save(cart);
    }
    // Hàm hỗ trợ so sánh 2 danh sách Option có giống hệt nhau không
    private boolean isSameOptions(List<ItemOption> list1, List<ItemOption> list2) {
        if (list1.size() != list2.size()) return false;
        List<Integer> ids1 = list1.stream().map(ItemOption::getOptionId).sorted().toList();
        List<Integer> ids2 = list2.stream().map(ItemOption::getOptionId).sorted().toList();
        return ids1.equals(ids2);
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
    @Transactional
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
}