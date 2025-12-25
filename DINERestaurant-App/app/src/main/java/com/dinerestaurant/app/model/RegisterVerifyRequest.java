package com.dinerestaurant.app.model;

public class RegisterVerifyRequest {
    private String phoneNumber;
    private String email;
    private String fullName;
    private String otp;

    public RegisterVerifyRequest(String phoneNumber, String email, String fullName, String otp) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.otp = otp;
    }
}