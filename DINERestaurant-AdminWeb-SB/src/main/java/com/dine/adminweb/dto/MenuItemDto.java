package com.dine.adminweb.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map; // Nhớ import Map
@Data
public class MenuItemDto {
    private Integer itemId;
    private String itemName;
    private String description;
    private BigDecimal price;
    private String image;
    private Boolean isAvailable;

    // Thay đổi ở đây: Thêm trường category dạng Map để hứng dữ liệu lồng nhau
    private Map<String, Object> category;

    // Trường này dùng để binding khi gửi form đi (giữ nguyên)
    private Integer categoryId;
    // Hàm getter thông minh để lấy tên category hiển thị ra bảng
    public String getCategoryName() {
        if (category != null && category.get("categoryName") != null) {
            return category.get("categoryName").toString();
        }
        return "N/A";
    }

    // Hàm này giúp form tự tick chọn đúng category khi nhấn Edit
    public Integer getCategoryId() {
        if (categoryId != null) return categoryId;
        if (category != null && category.get("categoryId") != null) {
            return (Integer) category.get("categoryId");
        }
        return null;
    }
}