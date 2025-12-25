package com.dine.adminweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ THÊM DÒNG NÀY
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Admin Home"); // ✅ giờ không lỗi
        return "index";
    }
}
