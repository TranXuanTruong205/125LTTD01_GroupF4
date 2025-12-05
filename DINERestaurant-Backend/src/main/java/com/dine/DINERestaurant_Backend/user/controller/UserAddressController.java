package com.dine.DINERestaurant_Backend.user.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.user.entity.UserAddress;
import com.dine.DINERestaurant_Backend.user.service.UserAddressService;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/addresses")
@CrossOrigin(origins = "*")
public class UserAddressController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserService userService;
    @Autowired private UserAddressService addressService;

    private User getCurrentUser(String authHeader) {
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return userService.getUserById(userId).orElseThrow();
    }

    // GET /users/addresses
    @GetMapping
    public Object getAll(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        return addressService.getAllByUser(user);
    }

    // GET /users/addresses/{id}
    @GetMapping("/{id}")
    public Object getById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        User user = getCurrentUser(authHeader);
        UserAddress address = addressService.getById(id);
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            return Map.of("error", "Địa chỉ không thuộc về người dùng");
        }
        return address;
    }

    //Get /users/addresses/default
    @GetMapping("/default")
    public Object getDefaultAddress(
            @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        UserAddress defaultAddress = addressService.getByDefaultAddress(user);
        if (defaultAddress == null) {
            return Map.of("message", "Chưa có địa chỉ mặc định");
        }
        return defaultAddress;
    }

    // POST /users/addresses
    @PostMapping
    public Object addAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserAddress address) {

        User user = getCurrentUser(authHeader);
        address.setUser(user);
        return addressService.save(address);
    }

    // PUT /users/addresses/{id}
    @PutMapping("/{id}")
    public Object updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody UserAddress updated) {

        User user = getCurrentUser(authHeader);
        updated.setAddressId(id);
        updated.setUser(user);
        return addressService.save(updated);
    }

    // DELETE /users/addresses/{id}
    @DeleteMapping("/{id}")
    public Object deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {

        getCurrentUser(authHeader); // chỉ kiểm tra user tồn tại
        addressService.delete(id);
        return Map.of("message", "Xóa địa chỉ thành công!");
    }

    // PUT /users/addresses/default/{id}
    @PutMapping("/default/{id}")
    public Object setDefault(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {

        User user = getCurrentUser(authHeader);
        addressService.setDefaultAddress(user, id);
        return Map.of("message", "Đặt địa chỉ mặc định thành công!");
    }
}
