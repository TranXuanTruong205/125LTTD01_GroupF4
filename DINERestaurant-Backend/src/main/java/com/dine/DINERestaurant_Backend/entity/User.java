package com.dine.DINERestaurant_Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;                 // ID người dùng

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;             // Số điện thoại

    @Column(name = "email")
    private String email;                   // Email

    @Column(name = "full_name")
    private String fullName;                // Họ và tên

    @Column(name = "gender")
    private String gender;                  // Giới tính: Nam / Nữ / Khác

    @Column(name = "address")
    private String address;                 // Địa chỉ

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;          // Ngày sinh

    @Column(name = "profile_picture")
    private String profilePicture;          // Ảnh đại diện

    @Column(name = "role")
    private String role = "customer";       // Vai trò: customer / admin

    @Column(name = "is_active")
    private Boolean isActive = true;        // Trạng thái tài khoản

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // Ngày tạo

    @Column(name = "last_login")
    private LocalDateTime lastLogin;        // Lần đăng nhập cuối
}
