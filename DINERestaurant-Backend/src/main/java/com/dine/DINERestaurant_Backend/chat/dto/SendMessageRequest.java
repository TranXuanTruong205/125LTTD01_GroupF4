package com.dine.DINERestaurant_Backend.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    private Integer conversationId;
    private Integer userId;      // chỉ dùng khi tạo conversation
    private String senderRole;   // customer / admin
    private String content;
}

