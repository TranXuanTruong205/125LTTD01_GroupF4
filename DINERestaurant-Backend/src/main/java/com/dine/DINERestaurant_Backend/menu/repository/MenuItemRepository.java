package com.dine.DINERestaurant_Backend.menu.repository;
import com.dine.DINERestaurant_Backend.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByCategory_CategoryId(Integer categoryId);
    // Tìm kiếm theo tên + mô tả
    List<MenuItem> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword,
            String descriptionKeyword
    );
}