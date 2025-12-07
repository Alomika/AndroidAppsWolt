package com.example.prifscourseandriod;

import static android.os.Build.VERSION_CODES.R;
import static com.example.prifscourseandriod.Constants.GET_ALL_RESTAURANTS_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Driver;
import com.example.prifscourseandriod.model.Restaurant;
import com.example.prifscourseandriod.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WoltMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wolt_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.messageList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");
        User connectedUser = new Gson().fromJson(userInfo, User.class);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        if(connectedUser instanceof BasicUser){
            executor.execute(() -> {
                try {
                    String response = RestOperations.sendGet(GET_ALL_RESTAURANTS_URL);
                    Log.d("DEBUG", "Restaurants JSON: " + response);
                    handler.post(() -> {
                        try {
                            if (!response.equals("Error")) {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
                                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
                                Gson gsonRestaurants = gsonBuilder.create();

                                Type restaurantListType = new TypeToken<List<Restaurant>>() {}.getType();
                                List<Restaurant> restaurantList = gsonRestaurants.fromJson(response, restaurantListType);

                                ListView restaurantListElement = findViewById(R.id.restaurantList);
                                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurantList);
                                restaurantListElement.setAdapter(adapter);

                                restaurantListElement.setOnItemClickListener((parent, view, position, id) -> {
                                    Restaurant selectedRestaurant = restaurantList.get(position);
                                    Log.d("DEBUG", "Selected restaurant: " + new Gson().toJson(selectedRestaurant));

                                    Intent intentMenu = new Intent(WoltMain.this, MenuActivity.class);
                                    intentMenu.putExtra("restaurantJsonObject", new Gson().toJson(selectedRestaurant));
                                    intentMenu.putExtra("userJsonObject", userInfo);
                                    startActivity(intentMenu);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else if (connectedUser instanceof Driver){
            executor.execute(() -> {
                try {
                    String response = RestOperations.sendGet(GET_ALL_RESTAURANTS_URL);
                    Log.d("DEBUG", "Restaurants JSON: " + response);
                    handler.post(() -> {
                        try {
                            if (!response.equals("Error")) {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
                                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
                                Gson gsonRestaurants = gsonBuilder.create();

                                Type restaurantListType = new TypeToken<List<Restaurant>>() {}.getType();
                                List<Restaurant> restaurantList = gsonRestaurants.fromJson(response, restaurantListType);

                                ListView restaurantListElement = findViewById(R.id.restaurantList);
                                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurantList);
                                restaurantListElement.setAdapter(adapter);

                                restaurantListElement.setOnItemClickListener((parent, view, position, id) -> {
                                    Restaurant selectedRestaurant = restaurantList.get(position);
                                    Log.d("DEBUG", "Selected restaurant: " + new Gson().toJson(selectedRestaurant));

                                    Intent intentMenu = new Intent(WoltMain.this, MenuActivity.class);
                                    intentMenu.putExtra("restaurantJsonObject", new Gson().toJson(selectedRestaurant));
                                    intentMenu.putExtra("userJsonObject", userInfo);
                                    startActivity(intentMenu);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else{
            finish();
        }

    }

    public void viewPurchaseHistory(View view) {
        Intent intent = new Intent(WoltMain.this, MyOrders.class);
        intent.putExtra("userJsonObject", getIntent().getStringExtra("userJsonObject"));
        startActivity(intent);
    }


    public void viewMyAccount(View view) {
        Intent intent = new Intent(WoltMain.this, Account.class);
        startActivity(intent);
    }

}
