package com.dine.DINERestaurant_Backend.cart.controller;
import com.dine.DINERestaurant_Backend.cart.entity.Cart;
import com.dine.DINERestaurant_Backend.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    @Autowired
    private CartService cartService;
    // Lấy giỏ hàng: GET /api/cart?phone=0987654321
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam String phone) {
        return ResponseEntity.ok(cartService.getCartByUser(phone));
    }
    // Thêm vào giỏ: POST /api/cart/add
    // Body: { "phoneNumber": "...", "menuItemId": 1, "quantity": 2 }
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody Map<String, Object> payload) {
        String phone = (String) payload.get("phoneNumber");
        Integer itemId = (Integer) payload.get("menuItemId");
        Integer quantity = (Integer) payload.get("quantity");
        return ResponseEntity.ok(cartService.addToCart(phone, itemId, quantity));
    }
    // Cập nhật số lượng: PUT /api/cart/update
    // Body: { "phoneNumber": "...", "cartItemId": 5, "quantity": 3 }
    @PutMapping("/update")
    public ResponseEntity<Cart> updateQuantity(@RequestBody Map<String, Object> payload) {
        String phone = (String) payload.get("phoneNumber");
        Integer cartItemId = (Integer) payload.get("cartItemId");
        Integer quantity = (Integer) payload.get("quantity");
        return ResponseEntity.ok(cartService.updateQuantity(phone, cartItemId, quantity));
    }
    // Xóa giỏ hàng: DELETE /api/cart/clear?phone=...
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam String phone) {
        cartService.clearCart(phone);
        return ResponseEntity.ok().build();
    }

    // API: Xóa 1 món khỏi giỏ
    // DELETE /api/cart/items/{cartItemId}?phone=...
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Integer cartItemId, @RequestParam String phone) {
        cartService.removeCartItem(phone, cartItemId);
        return ResponseEntity.ok().build();
    }
}