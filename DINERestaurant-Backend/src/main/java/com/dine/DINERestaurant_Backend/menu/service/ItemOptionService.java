package com.dine.DINERestaurant_Backend.menu.service;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import com.dine.DINERestaurant_Backend.menu.repository.ItemOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemOptionService {

    @Autowired
    private ItemOptionRepository itemOptionRepository;

    public List<ItemOption> getOptionsByMenuItem(Integer menuItemId) {
        return itemOptionRepository.findByMenuItem_ItemId(menuItemId);
    }
}
