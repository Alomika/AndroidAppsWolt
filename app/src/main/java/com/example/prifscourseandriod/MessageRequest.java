package com.example.prifscourseandriod;

public class MessageRequest {
    private String reviewText;
    private long ownerId;
    private long chatId;

    public MessageRequest(String reviewText, long ownerId, long chatId) {
        this.reviewText = reviewText;
        this.ownerId = ownerId;
        this.chatId = chatId;
    }

    // Getters and setters
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

    public long getChatId() { return chatId; }
    public void setChatId(long chatId) { this.chatId = chatId; }
}
