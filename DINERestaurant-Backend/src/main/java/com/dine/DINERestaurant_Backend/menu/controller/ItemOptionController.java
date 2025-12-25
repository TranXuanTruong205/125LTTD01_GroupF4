package com.dine.DINERestaurant_Backend.menu.controller;

import com.dine.DINERestaurant_Backend.menu.entity.ItemOption;
import com.dine.DINERestaurant_Backend.menu.service.ItemOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-options")
@CrossOrigin(origins = "*")
public class ItemOptionController {

    @Autowired
    private ItemOptionService itemOptionService;

    // Lấy danh sách topping của 1 món ăn
    @GetMapping("/menu-item/{itemId}")
    public ResponseEntity<List<ItemOption>> getOptionsByMenuItem(@PathVariable Integer itemId) {
        return ResponseEntity.ok(itemOptionService.getOptionsByMenuItem(itemId));
    }

    // Thêm mới topping cho món ăn
    @PostMapping("/menu-item/{itemId}")
    public ResponseEntity<ItemOption> createOption(@PathVariable Integer itemId, @RequestBody ItemOption option) {
        ItemOption saved = itemOptionService.createOption(itemId, option);
        return saved != null ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    // Cập nhật topping
    @PutMapping("/{id}")
    public ResponseEntity<ItemOption> updateOption(@PathVariable Integer id, @RequestBody ItemOption option) {
        ItemOption updated = itemOptionService.updateOption(id, option);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Xóa topping
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Integer id) {
        itemOptionService.deleteOption(id);
        return ResponseEntity.ok().build();
    }
}