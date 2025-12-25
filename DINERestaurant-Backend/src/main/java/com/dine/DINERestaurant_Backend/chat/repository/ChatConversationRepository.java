package com.dine.DINERestaurant_Backend.chat.repository;

import com.dine.DINERestaurant_Backend.chat.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, Integer> {

    Optional<ChatConversation> findByUser_UserId(Integer userId);
}
