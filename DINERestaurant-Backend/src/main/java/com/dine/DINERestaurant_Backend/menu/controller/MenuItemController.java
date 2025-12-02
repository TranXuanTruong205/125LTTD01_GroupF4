package com.dine.DINERestaurant_Backend.menu.controller;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import com.dine.DINERestaurant_Backend.menu.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/menu-items")
@CrossOrigin(origins = "*")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;
    // API: GET /api/menu-items (Lấy tất cả)
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }
    // API: GET /api/menu-items/category/{categoryId} (Lấy theo danh mục)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByCategory(categoryId));
    }

    // API: GET /api/menu-items/{id} (Lấy chi tiết)
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer id) {
        MenuItem item = menuItemService.getMenuItemById(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }
    // API: POST /api/menu-items (Thêm mới)
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(menuItemService.createMenuItem(menuItem));
    }
    // API: DELETE /api/menu-items/{id} (Xóa)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok().build();
    }
}