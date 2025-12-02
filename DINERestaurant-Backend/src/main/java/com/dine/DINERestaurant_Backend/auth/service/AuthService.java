package com.dine.DINERestaurant_Backend.auth.service;

import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean checkExist(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> login(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
