package com.dine.DINERestaurant_Backend.auth.controller;

import com.dine.DINERestaurant_Backend.auth.entity.User;
import com.dine.DINERestaurant_Backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phoneNumber");
        Optional<User> user = authService.login(phone);
        if (user.isPresent()) {
            return user.get();
        }
        return Map.of("error", "Số điện thoại chưa được đăng ký.");
    }

    @GetMapping("/check-phone")
    public boolean checkPhone(@RequestParam String phone) {
        return authService.login(phone).isPresent();
    }
}
