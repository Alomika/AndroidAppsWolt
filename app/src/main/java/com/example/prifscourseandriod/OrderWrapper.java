package com.example.prifscourseandriod;

import com.example.prifscourseandriod.model.FoodOrder;

import java.time.LocalDateTime;

// Wrapper class for sending orders
public class OrderWrapper {
    private String name;
    private int userId;
    private int restaurantId;
    private LocalDateTime date;
    private FoodOrder order;
    private Integer driverId; // nullable, driver can be null initially

    public OrderWrapper(String name, int userId, int restaurantId,
                        LocalDateTime date, FoodOrder order, Integer driverId) {
        this.name = name;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.date = date;
        this.order = order;
        this.driverId = driverId;
    }
}
