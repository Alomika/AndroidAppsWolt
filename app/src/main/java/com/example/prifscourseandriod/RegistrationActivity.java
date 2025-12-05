package com.example.prifscourseandriod;

import static com.example.prifscourseandriod.Constants.INSERT_NEW_DRIVER_URL;
import static com.example.prifscourseandriod.Constants.INSERT_NEW_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Driver;
import com.example.prifscourseandriod.model.VehicleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner vehicleSpinner = findViewById(R.id.viehicleTypeField);

// Get enum values
        VehicleType[] vehicleTypes = VehicleType.values();

// Create adapter
        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set adapter to Spinner
        vehicleSpinner.setAdapter(adapter);


    }

    public void createNewUser(View view) {
            TextView login = findViewById(R.id.loginField);
            TextView psw = findViewById(R.id.passwordField);
            TextView name = findViewById(R.id.nameField);
            TextView surname = findViewById(R.id.surnameField);
            TextView phone = findViewById(R.id.phoneNumberField);
            TextView address = findViewById(R.id.addressField);
            TextView license = findViewById(R.id.licenseField);
        TextView bday = findViewById(R.id.bdayField);
        String bDayString = bday.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(bDayString, formatter);
        String sendUrl;


        Spinner spinner = findViewById(R.id.viehicleTypeField);
        String selected = spinner.getSelectedItem().toString();
        VehicleType  viehicleType = VehicleType.valueOf(selected);
            //Patikrinti, ar buvo pasirinktas driver ar ne
            String userInfo = "{}";
        CheckBox driverCheckBox = findViewById(R.id.isDriver);
        if (driverCheckBox.isChecked()) {
                Driver driver = new Driver(login.getText().toString(), psw.getText().toString(), name.getText().toString(), surname.getText().toString(), phone.getText().toString(), address.getText().toString(),license.getText().toString(),birthDate, viehicleType);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateTimeSerializer())
                    .create();
            userInfo = gson.toJson(driver, Driver.class);
            sendUrl=INSERT_NEW_DRIVER_URL;
            System.out.println(userInfo);
        } else {

                BasicUser basicUser = new BasicUser(login.getText().toString(), psw.getText().toString(), name.getText().toString(), surname.getText().toString(), phone.getText().toString(), address.getText().toString());
                Gson gson = new Gson();
                userInfo = gson.toJson(basicUser, BasicUser.class);
                sendUrl = INSERT_NEW_USER_URL;
                System.out.println(userInfo);
            }


            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            String finalUserInfo = userInfo;
            executor.execute(() -> {
                try {
                    String response = RestOperations.sendPost(sendUrl, finalUserInfo);
                    handler.post(() -> {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (IOException e) {
                    //Toast reikes
                }

            });

        }
    }