package com.dine.adminweb.service;
import com.dine.adminweb.dto.MenuItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminMenuApiService {
    private final RestTemplate restTemplate;
    // URL của Backend
    private static final String BASE_URL = "http://localhost:8080/api/admin/menu-items";
    // URL lấy tất cả món (dùng tạm API customer nếu chưa có API admin list riêng)
    // Hoặc nếu Backend chưa có API list cho admin, anh/chị dùng tạm API menu public:
    private static final String PUBLIC_MENU_URL = "http://localhost:8080/api/menu-items";
    public AdminMenuApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public List<Map<String, Object>> getAllCategories() { // Đổi tên/kiểu trả về cho khớp Controller
        // Dùng API public để lấy danh sách
        String url = "http://localhost:8080/api/categories";
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();
    }
    public List<MenuItemDto> getAllMenuItems() {
        return Arrays.asList(
                restTemplate.getForObject(PUBLIC_MENU_URL, MenuItemDto[].class)
        );
    }
    public MenuItemDto getMenuItemById(Integer id) {
        return restTemplate.getForObject(PUBLIC_MENU_URL + "/" + id, MenuItemDto.class);
    }
    public void createMenuItem(MenuItemDto menuItem) {
        // CHUYỂN ĐỔI DTO -> JSON CON CẤU TRÚC BACKEND MONG ĐỢI
        Map<String, Object> payload = toBackendPayload(menuItem);
        restTemplate.postForObject(BASE_URL, payload, Map.class);
    }
    public void updateMenuItem(Integer id, MenuItemDto menuItem) {
        Map<String, Object> payload = toBackendPayload(menuItem);
        restTemplate.put(BASE_URL + "/" + id, payload);
    }
    public void deleteMenuItem(Integer id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
    private Map<String, Object> toBackendPayload(MenuItemDto dto) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", dto.getItemId());
        payload.put("itemName", dto.getItemName());
        payload.put("description", dto.getDescription());
        payload.put("price", dto.getPrice());
        payload.put("image", dto.getImage());
        payload.put("isAvailable", dto.getIsAvailable());

        // QUAN TRỌNG: Đóng gói categoryId thành object nested
        if (dto.getCategoryId() != null) {
            Map<String, Object> category = new HashMap<>();
            category.put("categoryId", dto.getCategoryId());
            payload.put("category", category);
        }

        return payload;
    }
}