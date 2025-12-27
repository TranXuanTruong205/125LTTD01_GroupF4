package com.dine.adminweb.controller;

import com.dine.adminweb.service.AdminDashboardApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ THÊM DÒNG NÀY
import org.springframework.web.bind.annotation.GetMapping;
@org.springframework.web.bind.annotation.RequestMapping("/admin/")
@Controller
public class AdminViewController {
    private final AdminDashboardApiService dashboardService;
    public AdminViewController(AdminDashboardApiService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/admin/orders";
    }
}
