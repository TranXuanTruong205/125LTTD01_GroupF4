package com.dine.DINERestaurant_Backend.user.service;


import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
