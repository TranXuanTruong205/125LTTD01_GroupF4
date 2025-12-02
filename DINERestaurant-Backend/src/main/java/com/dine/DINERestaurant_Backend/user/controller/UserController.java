package com.dine.DINERestaurant_Backend.user.controller;

import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;


    // Lấy thông tin người dùng
    @GetMapping("/me")
    public Object getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Map.of("error", "Thiếu hoặc sai định dạng Authorization header");
        }

        String token = authHeader.substring(7); // bỏ "Bearer "

        if (!jwtUtil.isTokenValid(token)) {
            return Map.of("error", "Token không hợp lệ hoặc đã hết hạn");
        }

        String phoneNumber = jwtUtil.extractPhoneNumber(token);

        Optional<User> user = userService.getUserByPhone(phoneNumber);
        if (user.isEmpty()) {
            return Map.of("error", "User không tồn tại");
        }

        return user.get();
    }




    // Cập nhật thông tin profile
    @PutMapping("/me")
    public Object updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Map.of("error", "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return Map.of("error", "Token không hợp lệ hoặc đã hết hạn");
        }

        String phoneNumber = jwtUtil.extractPhoneNumber(token);
        Optional<User> userOpt = userService.getUserByPhone(phoneNumber);

        if (userOpt.isEmpty()) {
            return Map.of("error", "User không tồn tại");
        }

        User user = userOpt.get();

        if (payload.containsKey("fullName")) {
            user.setFullName((String) payload.get("fullName"));
        }
        if (payload.containsKey("email")) {
            user.setEmail((String) payload.get("email"));
        }
        if (payload.containsKey("gender")) {
            user.setGender((String) payload.get("gender"));
        }
        if (payload.containsKey("address")) {
            user.setAddress((String) payload.get("address"));
        }
        if (payload.containsKey("dateOfBirth")) {
            user.setDateOfBirth(LocalDate.parse(payload.get("dateOfBirth").toString()));
        }
        if (payload.containsKey("profilePicture")) {
            user.setProfilePicture((String) payload.get("profilePicture"));
        }

        userService.updateUser(user);

        return Map.of("message", "Cập nhật hồ sơ thành công!");
    }

}
