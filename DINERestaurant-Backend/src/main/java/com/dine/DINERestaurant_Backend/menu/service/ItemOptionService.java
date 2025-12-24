package com.dine.DINERestaurant_Backend.menu.service;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import com.dine.DINERestaurant_Backend.menu.repository.ItemOptionRepository;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
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
    @Autowired
    private MenuItemRepository menuItemRepository; // Cần thêm repo này để tìm món ăn

    // 1. Lấy chi tiết một topping
    public ItemOption getOptionById(Integer id) {
        return itemOptionRepository.findById(id).orElse(null);
    }

    // 2. Thêm mới topping cho một món ăn cụ thể
    public ItemOption createOption(Integer menuItemId, ItemOption option) {
        return menuItemRepository.findById(menuItemId).map(menuItem -> {
            option.setMenuItem(menuItem);
            return itemOptionRepository.save(option);
        }).orElse(null);
    }

    // 3. Cập nhật topping
    public ItemOption updateOption(Integer id, ItemOption details) {
        ItemOption option = getOptionById(id);
        if (option != null) {
            option.setOptionName(details.getOptionName());
            option.setExtraPrice(details.getExtraPrice());
            return itemOptionRepository.save(option);
        }
        return null;
    }

    // 4. Xóa topping
    public void deleteOption(Integer id) {
        itemOptionRepository.deleteById(id);
    }
}
