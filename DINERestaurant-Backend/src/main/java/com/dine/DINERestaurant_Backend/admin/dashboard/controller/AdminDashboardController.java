package com.dine.DINERestaurant_Backend.admin.dashboard.controller;
import com.dine.DINERestaurant_Backend.admin.dashboard.dto.DashboardStats;
import com.dine.DINERestaurant_Backend.admin.dashboard.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    @Autowired private AdminDashboardService service;
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(service.getStats());
    }
}
