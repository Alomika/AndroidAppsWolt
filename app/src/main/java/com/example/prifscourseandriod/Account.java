package com.example.prifscourseandriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Driver;
import com.example.prifscourseandriod.model.VehicleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class Account extends AppCompatActivity {

    private EditText phoneField, addressField;
    private Spinner vehicleTypeField;
    private Button saveButton, deleteButton;

    private int userId;
    private BasicUser currentUser;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize views
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        vehicleTypeField = findViewById(R.id.vehicleTypeField);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get current user
        currentUser = getConnectedUser();

        if (currentUser == null) {
            // If no user data, close activity safely
            finish();
            return;
        }

        // Set EditText values safely
        phoneField.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        addressField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");

        // If user is Driver, setup vehicle type Spinner
        if (currentUser instanceof Driver) {
            VehicleType[] vehicleTypes = VehicleType.values();
            ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, vehicleTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vehicleTypeField.setAdapter(adapter);
            vehicleTypeField.setVisibility(View.VISIBLE);

            // Get actual vehicle type from user
            VehicleType userVehicleType = ((Driver) currentUser).getVehicleType();
            if (userVehicleType != null) {
                int spinnerPosition = adapter.getPosition(userVehicleType);
                if (spinnerPosition >= 0) vehicleTypeField.setSelection(spinnerPosition);
            }
        } else {
            vehicleTypeField.setVisibility(View.GONE);
        }
    }

    private BasicUser getConnectedUser() {
        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");
        if (userInfo == null) return null;
        return gson.fromJson(userInfo, BasicUser.class);
    }

    private void saveChanges() {
      }

    private void deleteAccount() {
    }
}