package com.dine.DINERestaurant_Backend.chat.repository;

import com.dine.DINERestaurant_Backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByConversation_ConversationIdOrderByCreatedAtAsc(Integer conversationId);
}
