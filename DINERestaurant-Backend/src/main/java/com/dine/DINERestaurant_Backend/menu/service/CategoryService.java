package com.dine.DINERestaurant_Backend.menu.service;
import com.dine.DINERestaurant_Backend.menu.entity.Category;
import com.dine.DINERestaurant_Backend.menu.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    // Lấy danh mục theo ID
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }
    // Thêm mới danh mục
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Xóa danh mục
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (category != null) {
            category.setCategoryName(categoryDetails.getCategoryName());
            category.setIcon(categoryDetails.getIcon());
            category.setDisplayOrder(categoryDetails.getDisplayOrder());
            return categoryRepository.save(category);
        }
        return null;
    }
}
