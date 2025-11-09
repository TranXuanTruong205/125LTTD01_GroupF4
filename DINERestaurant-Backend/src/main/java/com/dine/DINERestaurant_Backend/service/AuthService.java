package com.dine.DINERestaurant_Backend.service;

import com.dine.DINERestaurant_Backend.entity.User;
import com.dine.DINERestaurant_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public String register(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return "Số điện thoại đã được sử dụng.";
        }
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return "Đăng ký thành công!";
    }

    public Optional<User> login(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
}