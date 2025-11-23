package com.dine.DINERestaurant_Backend.auth.service;

import com.dine.DINERestaurant_Backend.auth.entity.OtpCode;
import com.dine.DINERestaurant_Backend.auth.repository.OtpCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    private final Random random = new Random();

    // Tạo và lưu OTP
    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", random.nextInt(999999));

        OtpCode otpCode = new OtpCode();
        otpCode.setPhoneNumber(phoneNumber);
        otpCode.setOtpCode(otp);
        otpCode.setCreatedAt(LocalDateTime.now());
        otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(3)); // OTP hết hạn sau 3 phút

        otpCodeRepository.save(otpCode);
        return otp;
    }

    // Xác minh OTP
    public boolean verifyOtp(String phoneNumber, String inputOtp) {
        Optional<OtpCode> optionalOtp = otpCodeRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        if (optionalOtp.isEmpty()) return false;

        OtpCode otp = optionalOtp.get();

        // Kiểm tra hết hạn
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Giới hạn số lần thử
        if (otp.getAttemptCount() >= 3) {
            return false;
        }

        otp.setAttemptCount(otp.getAttemptCount() + 1);

        if (otp.getOtpCode().equals(inputOtp)) {
            otp.setIsVerified(true);
            otpCodeRepository.save(otp);
            return true;
        } else {
            otpCodeRepository.save(otp);
            return false;
        }
    }
}
