package com.dine.adminweb.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String senderRole;
    private String content;
    private String createdAt;
}