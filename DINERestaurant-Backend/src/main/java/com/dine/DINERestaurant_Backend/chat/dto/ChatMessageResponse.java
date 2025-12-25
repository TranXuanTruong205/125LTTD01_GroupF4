package com.dine.DINERestaurant_Backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {
    private String senderRole;
    private String content;
    private LocalDateTime createdAt;
}
