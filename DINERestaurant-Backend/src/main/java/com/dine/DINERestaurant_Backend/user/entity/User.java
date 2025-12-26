package com.dine.DINERestaurant_Backend.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

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

    // ===== UNICODE SAFE =====
    @Nationalized
    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;             // Số điện thoại

    @Nationalized
    @Column(name = "email", length = 100)
    private String email;                   // Email

    @Nationalized
    @Column(name = "full_name", length = 100)
    private String fullName;                // Họ và tên

    @Nationalized
    @Column(name = "gender", length = 10)
    private String gender;                  // Giới tính: Nam / Nữ / Khác

    @Nationalized
    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;                 // Địa chỉ

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;          // Ngày sinh

    @Nationalized
    @Column(name = "profile_picture", length = 255)
    private String profilePicture;          // Ảnh đại diện

    @Nationalized
    @Column(name = "role", length = 20)
    private String role = "customer";       // Vai trò: customer / admin

    @Column(name = "is_active")
    private Boolean isActive = true;        // Trạng thái tài khoản

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // Ngày tạo

    @Column(name = "last_login")
    private LocalDateTime lastLogin;        // Lần đăng nhập cuối
}
