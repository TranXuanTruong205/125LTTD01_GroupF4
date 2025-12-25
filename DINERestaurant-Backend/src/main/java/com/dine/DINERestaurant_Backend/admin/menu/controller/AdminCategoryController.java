package com.dine.DINERestaurant_Backend.admin.menu.controller;

import com.dine.DINERestaurant_Backend.menu.entity.Category;
import com.dine.DINERestaurant_Backend.menu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "*")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    // ===============================
    // CREATE CATEGORY
    // POST /api/admin/categories
    // ===============================
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // ===============================
    // UPDATE CATEGORY
    // PUT /api/admin/categories/{id}
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Integer id,
            @RequestBody Category category
    ) {
        Category updated = categoryService.updateCategory(id, category);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    // ===============================
    // DELETE CATEGORY
    // DELETE /api/admin/categories/{id}
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
