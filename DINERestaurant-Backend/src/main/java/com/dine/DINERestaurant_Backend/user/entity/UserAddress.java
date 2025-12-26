package com.dine.DINERestaurant_Backend.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== UNICODE SAFE =====
    @Nationalized
    @Column(name = "label", length = 50, nullable = false)
    private String label;

    @Nationalized
    @Column(name = "address_text", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String addressText;

    private Double latitude;
    private Double longitude;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
