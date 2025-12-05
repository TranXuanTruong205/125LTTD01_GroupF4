package com.dine.DINERestaurant_Backend.auth.dto;

import com.dine.DINERestaurant_Backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}
