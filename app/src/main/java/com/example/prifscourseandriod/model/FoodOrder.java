package com.example.prifscourseandriod.model;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Double price;
    private BasicUser buyer;
    private Chat chat;
    private Driver driver;
    private Restaurant restaurant;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
    private List<Cuisine> cuisineList;


    public List<Cuisine> getCuisineList() {
        if (cuisineList == null) cuisineList = new ArrayList<>();
        return cuisineList;
    }

    public void setCuisineList(List<Cuisine> cuisineList) {
        this.cuisineList = cuisineList;
    }

    public FoodOrder(String name, Double price, BasicUser buyer, Restaurant restaurant, Driver driver) {
        this.name = name;
        this.price = price;
        this.buyer = buyer;
        this.restaurant = restaurant;
        this.driver = driver;
    }

    public FoodOrder(String name, Double price, BasicUser buyer, Restaurant restaurant, OrderStatus orderStatus) {
        this.name = name;
        this.price = price;
        this.buyer = buyer;
        this.restaurant = restaurant;
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return name + " Price: " + price + " Status: " + orderStatus;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public BasicUser getBuyer() {
        return buyer;
    }

    public void setBuyer(BasicUser buyer) {
        this.buyer = buyer;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
