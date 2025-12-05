package com.example.prifscourseandriod.model;

import java.time.LocalDate;

public class Review {
    private int id;
    private int rating;
    private String reviewText;
    private LocalDate dateCreated;
    private BasicUser commentOwner;
    private BasicUser feedbackUser;
    private Chat chat;

    public Review(String reviewText, BasicUser commentOwner, Chat chat) {
        this.reviewText = reviewText;
        this.commentOwner = commentOwner;
        this.chat = chat;
    }
    @Override
    public String toString() {
        return reviewText;
    }

}
