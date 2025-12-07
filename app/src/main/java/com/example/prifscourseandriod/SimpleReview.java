package com.example.prifscourseandriod;

import java.time.LocalDate;

public class SimpleReview {
    private String dateCreated;
    private String reviewText;
    private Integer commentOwnerId;
    private Integer chatId;

    public SimpleReview(String dateCreated, String reviewText, Integer commentOwnerId, Integer chatId) {
        this.dateCreated = dateCreated;
        this.reviewText = reviewText;
        this.commentOwnerId = commentOwnerId;
        this.chatId = chatId;
    }


    public String getDateCreated() {
        return dateCreated;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Integer getCommentOwnerId() {
        return commentOwnerId;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setCommentOwnerId(Integer commentOwnerId) {
        this.commentOwnerId = commentOwnerId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }
}
