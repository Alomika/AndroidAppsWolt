package com.example.prifscourseandriod.model;

import java.time.LocalDate;

import jakarta.persistence.Column;

public class Review {
    private int id;
    private int rating;
    private String reviewText;
    @Column(columnDefinition = "DATE")
    private LocalDate dateCreated;
    private BasicUser commentOwner;
    private BasicUser feedbackUser;
    private Chat chat;

    public Review(String reviewText, BasicUser commentOwner, Chat chat) {
        this.reviewText = reviewText;
        this.commentOwner = commentOwner;
        this.chat = chat;
    }
    public Review(int rating, String reviewText, BasicUser commentOwner, BasicUser feedbackUser) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.commentOwner = commentOwner;
        this.feedbackUser = feedbackUser;
    }

    public Review() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BasicUser getCommentOwner() {
        return commentOwner;
    }

    public void setCommentOwner(BasicUser commentOwner) {
        this.commentOwner = commentOwner;
    }

    public BasicUser getFeedbackUser() {
        return feedbackUser;
    }

    public void setFeedbackUser(BasicUser feedbackUser) {
        this.feedbackUser = feedbackUser;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return commentOwner.getName() + " " + commentOwner.getSurname() + ": " + reviewText;
    }

}
