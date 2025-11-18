package com.dinerestaurant.app.data;

import com.dinerestaurant.app.model.auth.User;

public class StaticData {

    // OTP tĩnh
    public static final String STATIC_OTP = "1234";

    // User tạm trong quá trình đăng ký
    public static User tempUser = new User();

    // User đã hoàn thành profile
    public static User currentUser = new User();

    // Kiểm tra xem có phải luồng đăng ký hay không
    public static boolean isRegisterFlow = false;
}