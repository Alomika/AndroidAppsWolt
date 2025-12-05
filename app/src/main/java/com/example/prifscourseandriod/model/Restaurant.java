package com.example.prifscourseandriod.model;

import java.util.List;

public class Restaurant extends User {
    private double rating;
    private List<Cuisine> menu;

    public Restaurant() { super(); }

    public Restaurant(String login, String password, String name, String surname,
                      String phoneNumber, double rating) {
        super(login, password, name, surname, phoneNumber);
        this.rating = rating;
    }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public List<Cuisine> getMenu() { return menu; }
    public void setMenu(List<Cuisine> menu) { this.menu = menu; }

    @Override
    public String toString() {
        return getName() + " - Rating: " + rating;
    }
}
