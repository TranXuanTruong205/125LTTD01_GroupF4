package com.dinerestaurant.app.model;

import com.dinerestaurant.app.data.remote.dto.UserDto;

public class LoginResponse {
    private String message;
    private String token;
    private UserDto user;

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDto getUser() { return user; }
}