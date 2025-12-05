package com.example.prifscourseandriod.model;

public class Cuisine {
    private int id;
    protected String name;
    protected String ingredients;
    protected Double price;
    protected boolean spicy = false;
    protected boolean vegan = false;
   // @JsonIgnore



    public Cuisine(String name, String ingredients, Double price, boolean spicy, boolean vegan, Restaurant restaurant) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.spicy = spicy;
        this.vegan = vegan;
    }
    public Cuisine() {
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

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isSpicy() {
        return spicy;
    }

    public void setSpicy(boolean spicy) {
        this.spicy = spicy;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    @Override
    public String toString() {
        return name + " " + ingredients + " " + price;
    }
}
