package com.dinerestaurant.app.model;

public class RegisterRequest {
    private String phoneNumber;
    private String email;
    private String fullName;

    public RegisterRequest(String phoneNumber, String email, String fullName) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
    }

}