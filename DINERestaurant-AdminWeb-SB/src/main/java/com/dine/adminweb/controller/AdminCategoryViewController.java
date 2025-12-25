package com.dine.adminweb.controller;
import com.dine.adminweb.service.AdminCategoryApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryViewController {
    private final AdminCategoryApiService categoryService;
    public AdminCategoryViewController(AdminCategoryApiService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Categories");
        model.addAttribute("content", "categories/index");
        model.addAttribute("categories", categoryService.getAllCategories());
        return "layout/main";
    }
    // FORM THÊM MỚI
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "New Category");
        model.addAttribute("content", "categories/form");
        // Dùng Map để hứng dữ liệu thay vì tạo DTO riêng cho tiết kiệm thời gian
        model.addAttribute("category", new java.util.HashMap<String, Object>());
        return "layout/main";
    }
    @PostMapping("/save")
    public String save(@RequestParam Map<String, Object> category) {
        // Lấy ID từ form (nếu có)
        String idStr = (String) category.get("categoryId");

        if (idStr == null || idStr.isEmpty()) {
            categoryService.create(category);
        } else {
            categoryService.update(Integer.parseInt(idStr), category);
        }
        return "redirect:/admin/categories";
    }
    // FORM SỬA
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("pageTitle", "Edit Category");
        model.addAttribute("content", "categories/form");

        // Lấy thông tin category hiện tại (tìm từ list getAll vì chưa có API getById)
        // Hoặc đơn giản là truyền ID vào và để user nhập lại tên (để nhanh gọn)
        // Nhưng tốt nhất là nên tìm trong list:
        var cat = categoryService.getAllCategories().stream()
                .filter(c -> String.valueOf(c.get("categoryId")).equals(String.valueOf(id)))
                .findFirst()
                .orElse(new java.util.HashMap<>());

        model.addAttribute("category", cat);
        return "layout/main";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}