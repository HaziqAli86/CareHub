package com.example.healthapp;

public class Message {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
    private boolean isRead;

    public Message() { } // Required for Firebase

    public Message(String senderId, String receiverId, String message, long timestamp, boolean isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public boolean isRead() { return isRead; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}