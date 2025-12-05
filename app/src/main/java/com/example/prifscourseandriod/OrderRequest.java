package com.example.prifscourseandriod;

import java.util.List;

public class OrderRequest {
    private String name;
    private Double price;
    private Integer buyerId;
    private Integer restaurantId;
    private Integer driverId; // nullable
    private List<Integer> cuisineIds;

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getBuyerId() { return buyerId; }
    public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }
    public Integer getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Integer restaurantId) { this.restaurantId = restaurantId; }
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public List<Integer> getCuisineIds() { return cuisineIds; }
    public void setCuisineIds(List<Integer> cuisineIds) { this.cuisineIds = cuisineIds; }
}
