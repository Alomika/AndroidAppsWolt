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

}
