package com.example.prifscourseandriod.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
public class Driver extends BasicUser{
    private String licence;
    @SerializedName("bDate")
    private LocalDate bDate;
    private VehicleType vehicleType;

    public Driver(String login, String password, String name, String surname, String phoneNumber, String address, String licence, LocalDate bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber, address);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

}
