package com.dine.adminweb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AdminCategoryApiService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminCategoryApiService(RestTemplate restTemplate,
                                   @Value("${backend.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<Map<String, Object>> getAll() {
        String url = baseUrl + "/api/admin/categories";
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();
    }

    public Map<String, Object> create(Map<String, Object> body) {
        String url = baseUrl + "/api/admin/categories";
        return restTemplate.postForObject(url, body, Map.class);
    }

    public void delete(Integer id) {
        String url = baseUrl + "/api/admin/categories/" + id;
        restTemplate.delete(url);
    }

    public Map<String, Object> update(Integer id, Map<String, Object> body) {
        String url = baseUrl + "/api/admin/categories/" + id;
        restTemplate.put(url, body);
        // nhiều API put không trả body -> tạm trả lại body cho UI (hoặc gọi GET lại)
        return body;
    }
}
