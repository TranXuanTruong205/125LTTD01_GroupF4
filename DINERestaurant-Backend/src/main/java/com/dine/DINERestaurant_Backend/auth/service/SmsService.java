package com.dine.DINERestaurant_Backend.auth.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String fromPhone;

    // Content SID từ template
    private static final String CONTENT_SID = "HX229f5a04fd0510ce1b071852155d3e75";

    public void sendOtpSms(String toPhone, String otp) {
        try {
            System.out.println("=== WhatsApp Template Message ===");
            System.out.println("Content SID: " + CONTENT_SID);
            System.out.println("To: " + toPhone);
            System.out.println("OTP: " + otp);

            Twilio.init(accountSid, authToken);

            // Format JSON string CHÍNH XÁC như Twilio yêu cầu
            String contentVariables = "{\"1\":\"" + otp + "\"}";

            System.out.println("Content Variables: " + contentVariables);

            // Gửi message với template
            Message message = Message.creator(
                            new PhoneNumber("whatsapp:" + toPhone),
                            new PhoneNumber("whatsapp:" + fromPhone),
                            "" // Body để trống
                    )
                    .setContentSid(CONTENT_SID)
                    .setContentVariables(contentVariables) // Pass JSON string
                    .create();
            Message delivered = Message.fetcher(message.getSid()).fetch();

            if (delivered.getStatus() == Message.Status.FAILED ||
                    delivered.getStatus() == Message.Status.UNDELIVERED) {

                throw new RuntimeException("Số điện thoại chưa đăng ký WhatsApp hoặc không hợp lệ.");
            }
            System.out.println("✅ Success!");
            System.out.println("Message SID: " + message.getSid());
            System.out.println("Status: " + message.getStatus());

        } catch (ApiException e) {
            String err = e.getMessage();
            System.err.println("❌ Error: " + err);

            if (err.contains("WhatsApp")
                    || err.contains("Whatsapp")
                    || err.contains("not a valid WhatsApp")
                    || err.contains("not a WhatsApp subscriber")) {

                throw new RuntimeException("Số điện thoại chưa đăng ký WhatsApp");
            }

            // Các lỗi khác
            throw new RuntimeException("Lỗi gửi OTP: " + err);

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Số điện thoại không hợp lệ", e);
        }
    }

}