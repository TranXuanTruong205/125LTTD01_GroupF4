package com.dine.DINERestaurant_Backend.auth.controller;

import com.dine.DINERestaurant_Backend.auth.dto.LoginResponse;
import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.auth.service.GoogleAuthService;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.auth.service.AuthService;
import com.dine.DINERestaurant_Backend.auth.service.OtpService;
import com.dine.DINERestaurant_Backend.auth.service.SmsService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GoogleAuthService googleAuthService;


    // ================= REGISTER STEP 1: SEND OTP =================
    @PostMapping("/register/request")
    public Object requestRegister(@RequestBody Map<String, String> payload) {

        String phone = payload.get("phoneNumber");
        String email = payload.get("email");
        String fullName = payload.get("fullName");

        if (phone == null || phone.isBlank() ||
                email == null || email.isBlank() ||
                fullName == null || fullName.isBlank()) {
            return Map.of("error", "Vui lòng nhập đầy đủ SĐT, Email và Họ tên");
        }

        // Check số điện thoại đã tồn tại
        if (authService.checkExist(phone)) {
            return Map.of("error", "Số điện thoại đã đăng ký tài khoản khác!");
        }

        // Check email đã tồn tại
        if (authService.getByEmail(email).isPresent()) {
            return Map.of("error", "Email đã tồn tại trong hệ thống!");
        }

        String fullPhone = "+" + phone;
        String otp = otpService.generateOtp(fullPhone);
        smsService.sendOtpSms(fullPhone, otp);

        return Map.of(
                "message", "OTP đã được gửi",
                "phoneNumber", phone,
                "email", email,
                "fullName", fullName
        );
    }



    // ================= REGISTER STEP 2: VERIFY OTP & CREATE USER =================
    @PostMapping("/register/verify")
    public Object verifyRegister(@RequestBody Map<String, String> payload) {

        String phone = payload.get("phoneNumber");
        String otp = payload.get("otp");
        String email = payload.get("email");
        String fullName = payload.get("fullName");

        if (phone == null || otp == null || email == null || fullName == null) {
            return Map.of("error", "Thiếu dữ liệu xác minh!");
        }

        String fullPhone = "+" + phone;

        boolean verified = otpService.verifyOtp(fullPhone, otp);
        if (!verified) {
            return Map.of("error", "OTP không hợp lệ hoặc đã hết hạn!");
        }

        if (authService.checkExist(phone)) {
            return Map.of("error", "Số điện thoại đã tồn tại!");
        }
        if (authService.getByEmail(email).isPresent()) {
            return Map.of("error", "Email đã tồn tại!");
        }

        User newUser = new User();
        newUser.setPhoneNumber(phone);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setIsActive(true);
        newUser.setRole("customer");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());

        newUser.setGender(payload.get("gender"));
        newUser.setAddress(payload.get("address"));
        if (payload.get("dateOfBirth") != null) {
            newUser.setDateOfBirth(LocalDate.parse(payload.get("dateOfBirth")));
        }
        newUser.setProfilePicture(payload.get("picture"));

        authService.saveUser(newUser);

        // Tạo token đăng nhập
        String token = jwtUtil.generateToken(newUser);

        return Map.of(
                "message", "Tạo tài khoản thành công!",
                "token", token,
                "user", newUser
        );
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

        Optional<User> userOpt = authService.login(phone);
        if (userOpt.isEmpty()) {
            return Map.of("error", "Không tìm thấy tài khoản!");
        }

        User user = userOpt.get();
        user.setLastLogin(LocalDateTime.now());
        authService.saveUser(user);

        String token = jwtUtil.generateToken(user);

        // Trả về token + user
        return new LoginResponse(token, user);
    }

    // ================= LOGIN GOOGLE =================
    @PostMapping("/login/google")
    public Object googleLogin(@RequestBody Map<String, String> payload) {

        String idToken = payload.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return Map.of("error", "Thiếu idToken");
        }

        GoogleIdToken.Payload tokenPayload = googleAuthService.verifyIdToken(idToken);
        if (tokenPayload == null) {
            return Map.of("error", "Google token không hợp lệ");
        }

        String email = tokenPayload.getEmail();
        String fullName = (String) tokenPayload.get("name");
        String picture = (String) tokenPayload.get("picture");

        // Kiểm tra user theo email
        Optional<User> userOpt = authService.getByEmail(email);

        if (userOpt.isPresent()) {
            // ĐÃ CÓ TÀI KHOẢN -> Đăng nhập luôn
            User user = userOpt.get();

            String token = jwtUtil.generateToken(user);

            user.setLastLogin(LocalDateTime.now());
            authService.saveUser(user);

            return Map.of(
                    "token", token,
                    "user", user
            );
        }

        // CHƯA CÓ TÀI KHOẢN -> YÊU CẦU NHẬP SĐT
        return Map.of(
                "email", email,
                "fullName", fullName,
                "profilePicture", picture,
                "requirePhoneNumber", true
        );
    }




}
