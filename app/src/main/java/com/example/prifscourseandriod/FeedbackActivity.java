package com.example.prifscourseandriod;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Driver;
import com.example.prifscourseandriod.model.FoodOrder;
import com.example.prifscourseandriod.model.Review;
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

public class FeedbackActivity extends AppCompatActivity {

    private ListView feedbackList;
    private EditText feedbackField;
    private Button feedBackButton;

    private FoodOrder currentOrder;
    private BasicUser connectedUser;
    private BasicUser reviewTargetUser;
    private int selectedRating = 0;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackList = findViewById(R.id.feedbackList);
        feedbackField = findViewById(R.id.feedbackField);
        feedBackButton = findViewById(R.id.feedBackButton);

        feedbackField.setHint("Write your review here...");

        Intent intent = getIntent();
        String userJson = intent.getStringExtra("userJsonObject");
        String orderJson = intent.getStringExtra("orderJsonObject");

        if (userJson != null) {
            connectedUser = gson.fromJson(userJson, BasicUser.class);
        } else {
            Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (orderJson != null) {
            currentOrder = gson.fromJson(orderJson, FoodOrder.class);
        } else {
            Toast.makeText(this, "Order data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        determineReviewTarget();
        setupRatingList();
        loadExistingReviews();
    }

    private void determineReviewTarget() {
        if (connectedUser instanceof Driver) {
            // Driver reviews the buyer
            reviewTargetUser = currentOrder.getBuyer();
            Toast.makeText(this, "Reviewing Customer: " + reviewTargetUser.getName() + " " +
                    reviewTargetUser.getSurname(), Toast.LENGTH_SHORT).show();
        } else {
            // Customer reviews driver
            if (currentOrder.getDriver() != null) {
                reviewTargetUser = currentOrder.getDriver();
                Toast.makeText(this, "Reviewing Driver: " + reviewTargetUser.getName() + " " +
                        reviewTargetUser.getSurname(), Toast.LENGTH_SHORT).show();
                feedBackButton.setEnabled(true);
            } else if (currentOrder.getDriver() != null) {
                // Fetch driver by ID dynamically
                fetchDriverById(currentOrder.getDriver().getId());
            } else {
                Toast.makeText(this, "No driver assigned to this order", Toast.LENGTH_SHORT).show();
                feedBackButton.setEnabled(false);
            }
        }
    }

    private void fetchDriverById(int driverId) {
        executor.execute(() -> {
            try {
                String url = String.format(Constants.GET_DRIVER_BY_ID, driverId); // e.g., "/api/drivers/%d"
                String response = RestOperations.sendGet(url);

                if (response != null && !response.isEmpty()) {
                    Driver driver = gson.fromJson(response, Driver.class);
                    currentOrder.setDriver(driver);
                    reviewTargetUser = driver;

                    handler.post(() -> {
                        Toast.makeText(FeedbackActivity.this,
                                "Reviewing Driver: " + driver.getName() + " " + driver.getSurname(),
                                Toast.LENGTH_SHORT).show();
                        feedBackButton.setEnabled(true);
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(FeedbackActivity.this, "Driver info not found", Toast.LENGTH_SHORT).show();
                        feedBackButton.setEnabled(false);
                    });
                }
            } catch (IOException e) {
                Log.e("FeedbackActivity", "Error fetching driver", e);
                handler.post(() -> {
                    Toast.makeText(FeedbackActivity.this,
                            "Network error fetching driver", Toast.LENGTH_SHORT).show();
                    feedBackButton.setEnabled(false);
                });
            }
        });
    }


    private void setupRatingList() {
        List<String> ratingOptions = new ArrayList<>();
        ratingOptions.add("⭐ 1 Star - Poor");
        ratingOptions.add("⭐⭐ 2 Stars - Fair");
        ratingOptions.add("⭐⭐⭐ 3 Stars - Good");
        ratingOptions.add("⭐⭐⭐⭐ 4 Stars - Very Good");
        ratingOptions.add("⭐⭐⭐⭐⭐ 5 Stars - Excellent");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, ratingOptions);
        feedbackList.setAdapter(adapter);
        feedbackList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        feedbackList.setOnItemClickListener((parent, view, position, id) -> {
            selectedRating = position + 1;
            feedbackList.setItemChecked(position, true);
            Toast.makeText(this, "Selected rating: " + selectedRating + " stars",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void loadExistingReviews() {
        if (reviewTargetUser == null) return;

        executor.execute(() -> {
            try {
                String url = String.format(Constants.GET_REVIEWS_BY_USER_ID, reviewTargetUser.getId());
                String response = RestOperations.sendGet(url);

                if (response != null && !response.isEmpty()) {
                    Type listType = new TypeToken<List<Review>>() {}.getType();
                    List<Review> reviews = gson.fromJson(response, listType);

                    handler.post(() -> {
                        if (reviews != null && !reviews.isEmpty()) {
                            displayExistingReviewsInfo(reviews);
                        }
                    });
                }

            } catch (IOException e) {
                Log.e("FeedbackActivity", "Network error loading reviews", e);
            } catch (Exception e) {
                Log.e("FeedbackActivity", "Error parsing reviews", e);
            }
        });
    }

    private void displayExistingReviewsInfo(List<Review> reviews) {
        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Review mostRecentReview = reviews.get(reviews.size() - 1);

        String info = String.format("\n\n--- Existing Reviews ---\n" +
                        "Average Rating: %.1f/5 (%d reviews)\n\n" +
                        "Most recent: \"%s\" - %d stars",
                avgRating, reviews.size(),
                mostRecentReview.getReviewText(),
                mostRecentReview.getRating());

        feedbackField.setHint("Write your review here..." + info);
    }

    public void sendFeedback(View view) {
        if (reviewTargetUser == null) {
            Toast.makeText(this, "No target user for review", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_LONG).show();
            return;
        }

        String reviewText = feedbackField.getText().toString().trim();

        executor.execute(() -> {
            try {
                String url = String.format(Constants.CREATE_REVIEW_FOR_ORDER, currentOrder.getId());
                String jsonPayload = String.format(
                        "{\"commentOwnerId\":%d,\"feedbackUserId\":%d,\"rating\":%d,\"reviewText\":\"%s\"}",
                        connectedUser.getId(),
                        reviewTargetUser.getId(),
                        selectedRating,
                        reviewText.replace("\"", "\\\"").replace("\n", "\\n")
                );

                String response = RestOperations.sendPost(url, jsonPayload);

                handler.post(() -> {
                    if (response != null) {
                        Toast.makeText(FeedbackActivity.this,
                                "Review submitted successfully! (" + selectedRating + " stars)",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                Log.e("FeedbackActivity", "Error submitting review", e);
                handler.post(() -> {
                    Toast.makeText(FeedbackActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    feedBackButton.setEnabled(true);
                });
            }
        });
    }
}
