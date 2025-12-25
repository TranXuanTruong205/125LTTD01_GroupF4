package com.dine.DINERestaurant_Backend.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    private String senderRole;   // customer / admin

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
