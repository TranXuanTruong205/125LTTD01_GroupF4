package com.dine.adminweb.controller;

import com.dine.adminweb.dto.AdminOrderDetailDto;
import com.dine.adminweb.dto.OrderDto;
import com.dine.adminweb.service.AdminOrderApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderViewController {

    private final AdminOrderApiService orderApiService;

    public AdminOrderViewController(AdminOrderApiService orderApiService) {
        this.orderApiService = orderApiService;
    }

    // =========================
    // LIST ORDERS
    // =========================
    @GetMapping
    public String ordersPage(
            @RequestParam(defaultValue = "all") String tab,
            Model model
    ) {
        model.addAttribute("pageTitle", "Orders");
        model.addAttribute("content", "orders/index");

        List<?> orders;
        switch (tab) {
            case "active" -> orders = orderApiService.getActiveOrders();
            case "history" -> orders = orderApiService.getOrderHistory();
            default -> orders = orderApiService.getAllOrders();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("tab", tab);

        return "layout/main";
    }

    // =========================
    // ORDER DETAIL
    // =========================
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {

        AdminOrderDetailDto order = orderApiService.getOrderById(id);

        model.addAttribute("pageTitle", "Order #" + id);
        model.addAttribute("content", "orders/detail");
        model.addAttribute("order", order);

        return "layout/main";
    }

    // =========================
    // UPDATE STATUS (FORM POST)
    // =========================
    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Integer id,
            @RequestParam String status
    ) {
        orderApiService.updateOrderStatus(id, status);
        return "redirect:/admin/orders/" + id;
    }
}
