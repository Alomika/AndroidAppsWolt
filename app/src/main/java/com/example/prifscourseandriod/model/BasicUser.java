package com.example.prifscourseandriod.model;

public class BasicUser extends User {
    private String address;

    public BasicUser() { super(); }

    public BasicUser(String login, String password, String name, String surname,
                     String phoneNumber, String address) {
        super(login, password, name, surname, phoneNumber);
        this.address = address;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return getName() + " " + getSurname() + " - " + address;
    }
}
