package com.dine.DINERestaurant_Backend.auth.controller;

import com.dine.DINERestaurant_Backend.auth.service.OtpService;
import com.dine.DINERestaurant_Backend.auth.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public String sendOtp(@RequestParam String phoneNumber) {
        String phone = "+"+phoneNumber;
        String otp = otpService.generateOtp(phone);
        smsService.sendOtpSms(phone, otp);
        return "Mã OTP đã được gửi đến số " + phone;
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        String phone = "+"+phoneNumber;
        boolean verified = otpService.verifyOtp(phone, otp);
        return verified ? "Xác minh OTP thành công!" : "Mã OTP không hợp lệ hoặc đã hết hạn!";
    }
}
