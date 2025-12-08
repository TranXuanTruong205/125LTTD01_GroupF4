package com.dinerestaurant.app.model;

public class ChatMessage {
    private String message;
    private String time;
    private boolean isSentByMe;
    private boolean isRead;

    public ChatMessage(String message, String time, boolean isSentByMe, boolean isRead) {
        this.message = message;
        this.time = time;
        this.isSentByMe = isSentByMe;
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        isSentByMe = sentByMe;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
