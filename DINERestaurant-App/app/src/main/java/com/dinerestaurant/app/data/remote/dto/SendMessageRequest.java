package com.dinerestaurant.app.data.remote.dto;

public class SendMessageRequest {
    private int conversationId;
    private String content;

    public SendMessageRequest(int conversationId, String content) {
        this.conversationId = conversationId;
        this.content = content;
    }
}
