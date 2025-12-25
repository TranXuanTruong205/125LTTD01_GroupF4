package com.dine.adminweb.service;

import com.dine.adminweb.dto.AdminOrderDetailDto;
import com.dine.adminweb.dto.OrderDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AdminOrderApiService {

    private final RestTemplate restTemplate;

    public AdminOrderApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String BASE_URL = "http://localhost:8080/api/admin/orders";

    public List<OrderDto> getAllOrders() {
        return Arrays.asList(
                restTemplate.getForObject(BASE_URL, OrderDto[].class)
        );
    }

    public List<OrderDto> getActiveOrders() {
        return Arrays.asList(
                restTemplate.getForObject(BASE_URL + "/active", OrderDto[].class)
        );
    }

    public List<OrderDto> getOrderHistory() {
        return Arrays.asList(
                restTemplate.getForObject(BASE_URL + "/history", OrderDto[].class)
        );
    }

    public AdminOrderDetailDto getOrderById(Integer id) {
        return restTemplate.getForObject(
                BASE_URL + "/" + id,
                AdminOrderDetailDto.class
        );
    }
    public void updateOrderStatus(Integer orderId, String status) {
        String url = BASE_URL + "/" + orderId + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("status", status);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }
}
