package com.example.prifscourseandriod.model;


import java.time.LocalDate;

public class Chat {
    private int id;

    private String name;
    private String chatText;
    private LocalDate dateCreated;
    public Chat(String name, FoodOrder foodOrder) {
        this.name = name;
        this.dateCreated = LocalDate.now();
    }
public Chat(){

}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }
}
