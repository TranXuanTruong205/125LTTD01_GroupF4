package com.dine.DINERestaurant_Backend.menu.service;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    // Lấy tất cả món ăn
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }
    // Lấy món ăn theo danh mục
    public List<MenuItem> getMenuItemsByCategory(Integer categoryId) {
        return menuItemRepository.findByCategory_CategoryId(categoryId);
    }

    // Lấy chi tiết món ăn
    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id).orElse(null);
    }
    // Thêm món ăn mới
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }
    // Xóa món ăn
    public void deleteMenuItem(Integer id) {
        menuItemRepository.deleteById(id);
    }
}