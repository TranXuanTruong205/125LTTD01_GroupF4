package com.dine.DINERestaurant_Backend.auth.controller;

import com.dine.DINERestaurant_Backend.auth.entity.User;
import com.dine.DINERestaurant_Backend.auth.service.AuthService;
import com.dine.DINERestaurant_Backend.auth.service.OtpService;
import com.dine.DINERestaurant_Backend.auth.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private SmsService smsService;


    // ================= REGISTER STEP 1: SEND OTP =================
    @PostMapping("/register/request")
    public Object requestRegister(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phoneNumber");

        if (authService.checkExist(phone)) {
            return Map.of("error", "Số điện thoại đã tồn tại!");
        }

        String fullPhone = "+" + phone;
        String otp = otpService.generateOtp(fullPhone);
        smsService.sendOtpSms(fullPhone, otp);

        return Map.of("message", "OTP đã được gửi đến " + phone);
    }


    // ================= REGISTER STEP 2: VERIFY OTP & CREATE USER =================
    @PostMapping("/register/verify")
    public Object verifyRegister(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phoneNumber");
        String otp = payload.get("otp");

        boolean verified = otpService.verifyOtp("+" + phone, otp);
        if (!verified) {
            return Map.of("error", "OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Create new user with phone only
        User newUser = new User();
        newUser.setPhoneNumber(phone);
        newUser.setIsActive(true);
        newUser.setRole("customer");
        newUser.setCreatedAt(LocalDateTime.now());
        authService.saveUser(newUser);

        return Map.of("message", "Tạo tài khoản thành công!");
    }


    // ================= LOGIN STEP 1: SEND OTP =================
    @PostMapping("/login/request")
    public Object requestLogin(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phoneNumber");

        Optional<User> user = authService.login(phone);
        if (user.isEmpty()) {
            return Map.of("error", "Số điện thoại chưa được đăng ký!");
        }

        if (!Boolean.TRUE.equals(user.get().getIsActive())) {
            return Map.of("error", "Tài khoản bị khóa.");
        }

        String fullPhone = "+" + phone;
        String otp = otpService.generateOtp(fullPhone);
        smsService.sendOtpSms(fullPhone, otp);

        return Map.of("message", "Đã gửi OTP đăng nhập đến " + phone);
    }


    // ================= LOGIN STEP 2: VERIFY OTP =================
    @PostMapping("/login/verify")
    public Object verifyLogin(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phoneNumber");
        String otp = payload.get("otp");

        boolean verified = otpService.verifyOtp("+" + phone, otp);
        if (!verified) {
            return Map.of("error", "OTP không hợp lệ hoặc đã hết hạn!");
        }

        Optional<User> user = authService.login(phone);
        if (user.isEmpty()) {
            return Map.of("error", "Không tìm thấy tài khoản!");
        }

        user.get().setLastLogin(LocalDateTime.now());
        authService.saveUser(user.get());

        return user.get();
    }

}
