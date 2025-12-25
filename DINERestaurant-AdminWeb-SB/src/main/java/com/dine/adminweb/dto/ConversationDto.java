package com.dine.adminweb.dto;

import lombok.Data;

@Data
public class ConversationDto {
    private Integer conversationId;
    private UserDto user;
    private String lastMessage;
}
