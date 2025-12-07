package com.dine.DINERestaurant_Backend.promotions.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Data   // Lombok tạo tự động getId(), getTitle(), getIsActive()...
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    // ID kiểu Integer cho khớp DB (INT)
    private Integer id;                      // ==> getId()

    @Column(name = "title", nullable = false)
    private String title;                    // ==> getTitle()

    @Column(name = "description")
    private String description;              // ==> getDescription()

    @Column(name = "image")
    private String image;                    // ==> getImage()

    @Column(name = "discount_percent")
    private Integer discountPercent;         // ==> getDiscountPercent()

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;             // ==> getStartDate()

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;               // ==> getEndDate()

    @Column(name = "is_active")
    private Boolean isActive;                // ==> getIsActive()
}
