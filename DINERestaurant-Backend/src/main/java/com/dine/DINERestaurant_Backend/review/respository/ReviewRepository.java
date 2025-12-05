package com.dine.DINERestaurant_Backend.review.respository;

import com.dine.DINERestaurant_Backend.review.DTO.ReviewResponse;
import com.dine.DINERestaurant_Backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy review theo món
    List<Review> findByItemIdOrderByCreatedAtDesc(Long itemId);

    // Lấy review của 1 user
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Check user này đã từng mua món này chưa (dùng native query lên bảng orders + order_details)
    @Query(value = """
        SELECT COUNT(*)
        FROM orders o
        JOIN order_details od ON o.order_id = od.order_id
        WHERE o.user_id = :userId
          AND od.item_id = :itemId
          AND o.order_status IN (N'Hoàn thành', N'Đã xác nhận')
        """, nativeQuery = true)
int countUserPurchasedItem(@Param("userId") Long userId,
                             @Param("itemId") Long itemId);

}
