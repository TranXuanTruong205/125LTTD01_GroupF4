package com.dine.DINERestaurant_Backend.user.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    private String label;

    @Column(name = "address_text", nullable = false)
    private String addressText;

    private Double latitude;
    private Double longitude;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
