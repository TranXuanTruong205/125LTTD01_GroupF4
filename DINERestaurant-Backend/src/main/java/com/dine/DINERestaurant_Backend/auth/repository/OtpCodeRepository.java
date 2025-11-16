package com.dine.DINERestaurant_Backend.auth.repository;

import com.dine.DINERestaurant_Backend.auth.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
