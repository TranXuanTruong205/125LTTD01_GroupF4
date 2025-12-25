package com.dine.DINERestaurant_Backend.chat.entity;

import com.dine.DINERestaurant_Backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversations")
@Getter
@Setter
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer conversationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String lastMessage;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
