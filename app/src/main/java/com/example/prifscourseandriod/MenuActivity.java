package com.example.prifscourseandriod;

import static com.example.prifscourseandriod.Constants.GET_CUISINE_BY_ID_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Cuisine;
import com.example.prifscourseandriod.model.Driver;
import com.example.prifscourseandriod.model.Restaurant;
import com.example.prifscourseandriod.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MenuActivity extends AppCompatActivity {

    private BasicUser currentUser;
    private Restaurant currentRestaurant;
    private Driver currentDriver;
    private List<Cuisine> selectedCuisines = new ArrayList<>();
    private ArrayAdapter<Cuisine> selectedAdapter;

    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Safe retrieval of intent extras
        String userJson = getIntent().getStringExtra("userJsonObject");
        String restaurantJson = getIntent().getStringExtra("restaurantJsonObject");

        if (userJson == null || restaurantJson == null) {
            Toast.makeText(this, "User or restaurant data missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        currentUser = new Gson().fromJson(userJson, BasicUser.class);
        currentRestaurant = new Gson().fromJson(restaurantJson, Restaurant.class);

        if (currentRestaurant == null || currentUser == null) {
            Toast.makeText(this, "Failed to parse user or restaurant", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ListView menuListView = findViewById(R.id.menuList);
        ListView selectedListView = findViewById(R.id.selectedList);

        // Adapter for selected cuisines
        selectedAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, selectedCuisines);
        selectedListView.setAdapter(selectedAdapter);

        // Click to remove from selected list
        selectedListView.setOnItemClickListener((parent, view, position, id) -> {
            Cuisine removed = selectedCuisines.remove(position);
            selectedAdapter.notifyDataSetChanged();
            Toast.makeText(MenuActivity.this,
                    removed.getName() + " removed from order",
                    Toast.LENGTH_SHORT).show();
        });

        // Load cuisines safely
        loadCuisines(menuListView);
    }

    private void loadCuisines(ListView menuListView) {
        String url = String.format(GET_CUISINE_BY_ID_URL, currentRestaurant.getId());
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(url);
                handler.post(() -> {
                    if (!"Error".equals(response) && response != null && !response.isEmpty()) {
                        try {
                            Type type = new TypeToken<List<Cuisine>>() {}.getType();
                            List<Cuisine> cuisines = new Gson().fromJson(response, type);
                            if (cuisines == null) cuisines = new ArrayList<>();

                            ArrayAdapter<Cuisine> menuAdapter = new ArrayAdapter<>(MenuActivity.this,
                                    android.R.layout.simple_list_item_1, cuisines);
                            menuListView.setAdapter(menuAdapter);

                            List<Cuisine> finalCuisines = cuisines;
                            menuListView.setOnItemClickListener((parent, view, position, id) -> {
                                Cuisine selected = finalCuisines.get(position);
                                if (selected != null) {
                                    if (selectedCuisines.contains(selected)) {
                                        selectedCuisines.remove(selected);
                                        Toast.makeText(MenuActivity.this,
                                                selected.getName() + " removed from order", Toast.LENGTH_SHORT).show();
                                    } else {
                                        selectedCuisines.add(selected);
                                        Toast.makeText(MenuActivity.this,
                                                selected.getName() + " added to order", Toast.LENGTH_SHORT).show();
                                    }
                                    selectedAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse cuisines", e);
                            Toast.makeText(MenuActivity.this, "Error parsing menu data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "Failed to load cuisines", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                handler.post(() -> Toast.makeText(MenuActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void placeOrder(View view) {
        if (selectedCuisines.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Integer buyerId = Optional.ofNullable(currentUser)
                    .map(BasicUser::getId)
                    .orElseThrow(() -> new IllegalStateException("User not set"));

            Integer restaurantId = Optional.ofNullable(currentRestaurant)
                    .map(Restaurant::getId)
                    .orElseThrow(() -> new IllegalStateException("Restaurant not set"));

            Integer driverId = Optional.ofNullable(currentDriver)
                    .map(Driver::getId)
                    .orElse(null);

            String name = "Order#" + (new Random().nextInt(90000) + 10000);
            double price = selectedCuisines.stream()
                    .mapToDouble(Cuisine::getPrice)
                    .sum();

            OrderRequest req = new OrderRequest();
            req.setName(name);
            req.setPrice(price);
            req.setBuyerId(buyerId);
            req.setRestaurantId(restaurantId);
            req.setDriverId(driverId);
            req.setCuisineIds(selectedCuisines.stream()
                    .map(Cuisine::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    Gson gson = new Gson();
                    String orderJson = gson.toJson(req);
                    String response = RestOperations.sendPost(Constants.INSERT_NEW_FOODORDER_URL, orderJson);
                    handler.post(() -> {
                        if (!"Error".equals(response)) {
                            Toast.makeText(MenuActivity.this, "Order placed: " + name, Toast.LENGTH_LONG).show();
                            selectedCuisines.clear();
                            selectedAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MenuActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Network error placing order", e);
                    handler.post(() -> Toast.makeText(MenuActivity.this, "Network error", Toast.LENGTH_SHORT).show());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Cannot place order", e);
            Toast.makeText(this, "Cannot place order: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
