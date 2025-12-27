package com.dine.adminweb.service;
import com.dine.adminweb.dto.DashboardStatsDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class AdminDashboardApiService {
    private final RestTemplate restTemplate;

    // URL Backend
    private final String API_URL = "http://localhost:8080/api/admin/dashboard/stats";
    public AdminDashboardApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public DashboardStatsDto getStats() {
        try {
            return restTemplate.getForObject(API_URL, DashboardStatsDto.class);
        } catch (Exception e) {
            return new DashboardStatsDto(); // Return empty neu loi
        }
    }
}