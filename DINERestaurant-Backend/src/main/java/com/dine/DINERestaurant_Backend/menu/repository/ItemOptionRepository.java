package com.dine.DINERestaurant_Backend.menu.repository;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOptionRepository extends JpaRepository<ItemOption, Integer> {
    List<ItemOption> findByMenuItem_ItemId(Integer itemId);
}
