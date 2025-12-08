package com.dine.DINERestaurant_Backend.order.repository;

import com.dine.DINERestaurant_Backend.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
}