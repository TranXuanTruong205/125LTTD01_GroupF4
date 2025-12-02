package com.dine.DINERestaurant_Backend.menu.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;
    // Quan hệ Many-to-One với Category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "item_name", nullable = false)
    private String itemName;
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "discount_price")
    private BigDecimal discountPrice;
    @Column(name = "image")
    private String image;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "total_reviews")
    private Integer totalReviews;
    @Column(name = "is_available")
    private Boolean isAvailable;
}