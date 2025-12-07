package com.dine.DINERestaurant_Backend.menu.entity;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    @Column(name = "icon")
    private String icon;
    @Column(name = "display_order")
    private Integer displayOrder;
}