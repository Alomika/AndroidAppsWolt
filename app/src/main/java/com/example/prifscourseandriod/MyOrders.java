package com.example.prifscourseandriod;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Cuisine;
import com.example.prifscourseandriod.model.FoodOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyOrders extends AppCompatActivity {

    private ListView ordersListView;
    private ListView cuisineOrderList;
    private ArrayAdapter<String> ordersAdapter;
    private ArrayAdapter<String> cuisineAdapter;
    private List<FoodOrder> orders;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);

        ordersListView = findViewById(R.id.myOrderList);
        cuisineOrderList = findViewById(R.id.cuisineOrderList);

        BasicUser connectedUser = getConnectedUser();
        if (connectedUser != null) {
            fetchOrdersForUser(connectedUser.getId());
        } else {
            Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show();
        }
    }

    private BasicUser getConnectedUser() {
        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");
        if (userInfo == null) return null;
        return gson.fromJson(userInfo, BasicUser.class);
    }

    private void fetchOrdersForUser(int buyerId) {
        executor.execute(() -> {
            String url = String.format(Constants.GET_ORDERS_BY_BUYER_ID, buyerId);
            Log.d("MyOrders", "Fetching orders from URL: " + url);

            try {
                String response = RestOperations.sendGet(url);
                if (response == null) {
                    Log.e("MyOrders", "Response is null. Check backend or network.");
                    showToast("Failed to fetch orders");
                    return;
                }
                Log.d("MyOrders", "Response: " + response);

                orders = parseOrders(response);
                handler.post(this::populateOrdersListView);

            } catch (IOException e) {
                Log.e("MyOrders", "Network error", e);
                showToast("Network error");
            }
        });
    }

    private List<FoodOrder> parseOrders(String response) {
        Type listType = new TypeToken<List<FoodOrder>>() {}.getType();
        return gson.fromJson(response, listType);
    }

    private void populateOrdersListView() {
        if (orders == null || orders.isEmpty()) {
            Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> orderNames = new ArrayList<>();
        for (FoodOrder o : orders) {
            orderNames.add(o.getName() + " - " + o.getOrderStatus() + " - " + o.getDateCreated());
        }

        ordersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderNames);
        ordersListView.setAdapter(ordersAdapter);

        ordersListView.setOnItemClickListener((parent, view, position, id) -> showCuisinesForOrder(orders.get(position)));
    }

    private void showCuisinesForOrder(FoodOrder order) {
        int orderId = order.getId();

        executor.execute(() -> {
            try {
                String url = String.format(Constants.GET_CUISINE_BY_ORDER_ID, orderId);
                String response = RestOperations.sendGet(url);

                Type listType = new TypeToken<List<Cuisine>>() {}.getType();
                List<Cuisine> cuisines = new Gson().fromJson(response, listType);

                List<String> cuisineDetails = new ArrayList<>();
                if (cuisines == null || cuisines.isEmpty()) {
                    cuisineDetails.add("No items in this order");
                } else {
                    for (Cuisine c : cuisines) {
                        String details = c.getName() + " - " + c.getIngredients() + " - $" + c.getPrice();
                        if (c.isSpicy()) details += " 🌶";
                        if (c.isVegan()) details += " 🌱";
                        cuisineDetails.add(details);
                    }
                }

                handler.post(() -> {
                    cuisineAdapter = new ArrayAdapter<>(MyOrders.this,
                            android.R.layout.simple_list_item_1, cuisineDetails);
                    cuisineOrderList.setAdapter(cuisineAdapter);
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(MyOrders.this, "Failed to fetch cuisines", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void showToast(String message) {
        handler.post(() -> Toast.makeText(MyOrders.this, message, Toast.LENGTH_SHORT).show());
    }

    public void backToMain(View view) {
        startActivity(new Intent(MyOrders.this, WoltMain.class));
    }
}
