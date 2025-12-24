package com.dine.DINERestaurant_Backend.menu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "item_options")
public class ItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "extra_price")
    private BigDecimal extraPrice;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private MenuItem menuItem;

    @Column(name = "option_name", nullable = false)
    private String optionName;


}
