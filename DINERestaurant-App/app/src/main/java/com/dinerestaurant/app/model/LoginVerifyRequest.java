package com.dinerestaurant.app.model;

public class LoginVerifyRequest {
    private String phoneNumber;
    private String otp;

    public LoginVerifyRequest(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
    }
}
