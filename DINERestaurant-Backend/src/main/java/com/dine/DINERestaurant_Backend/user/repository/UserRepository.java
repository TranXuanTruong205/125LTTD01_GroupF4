package com.dine.DINERestaurant_Backend.user.repository;

import com.dine.DINERestaurant_Backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUserId(Integer userId);
    boolean existsByPhoneNumber(String phoneNumber);
}