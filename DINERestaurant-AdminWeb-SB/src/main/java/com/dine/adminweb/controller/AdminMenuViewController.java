package com.dine.adminweb.controller;
import com.dine.adminweb.dto.MenuItemDto;
import com.dine.adminweb.service.AdminMenuApiService;
import com.dine.adminweb.service.AdminCategoryApiService; // Giả sử đã có service này để lấy danh mục
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/admin/menu")
public class AdminMenuViewController {
    private final AdminMenuApiService menuService;
    private final AdminCategoryApiService categoryService;
    public AdminMenuViewController(AdminMenuApiService menuService, AdminCategoryApiService categoryService) {
        this.menuService = menuService;
        this.categoryService = categoryService;
    }
    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Menu Management");
        model.addAttribute("content", "menu/index");
        model.addAttribute("menuItems", menuService.getAllMenuItems());
        return "layout/main";
    }
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "Add New Item");
        model.addAttribute("content", "menu/form");
        model.addAttribute("menuItem", new MenuItemDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "layout/main";
    }
    @PostMapping("/save")
    public String save(@ModelAttribute MenuItemDto menuItem) {
        if (menuItem.getItemId() == null) {
            menuService.createMenuItem(menuItem);
        } else {
            menuService.updateMenuItem(menuItem.getItemId(), menuItem);
        }
        return "redirect:/admin/menu";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("pageTitle", "Edit Item");
        model.addAttribute("content", "menu/form");
        model.addAttribute("menuItem", menuService.getMenuItemById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "layout/main";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        menuService.deleteMenuItem(id);
        return "redirect:/admin/menu";
    }
}