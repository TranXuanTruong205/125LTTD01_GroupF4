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

    @Autowired
    private com.dine.DINERestaurant_Backend.menu.service.ItemOptionService itemOptionService;
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


    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenuItems(@RequestParam String keyword) {
        // Lưu ý: Cần đảm bảo MenuItemRepository đã có hàm tìm kiếm (xem lại hướng dẫn trước nếu chưa có)
        return ResponseEntity.ok(menuItemService.searchMenuItems(keyword));
    }
    @GetMapping("/{id}/options")
    public ResponseEntity<List<Object>> getMenuOptions(@PathVariable Integer id) {
        // Gọi service để lấy danh sách thật
        List<com.dine.DINERestaurant_Backend.menu.entity.ItemOption> options = itemOptionService.getOptionsByMenuItem(id);
        return ResponseEntity.ok((List) options);
    }

}