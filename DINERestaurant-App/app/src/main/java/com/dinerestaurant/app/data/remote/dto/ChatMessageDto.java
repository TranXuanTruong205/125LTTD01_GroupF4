package com.dinerestaurant.app.data.remote.dto;

public class ChatMessageDto {
    private String senderRole;   // customer / admin
    private String content;
    private String createdAt;

    public String getSenderRole() { return senderRole; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
