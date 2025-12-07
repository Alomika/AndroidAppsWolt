package com.example.prifscourseandriod;

import static com.example.prifscourseandriod.Constants.GET_MESSAGES_BY_CHAT_ID;
import static com.example.prifscourseandriod.Constants.SEND_MESSAGE_TO_CHAT;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prifscourseandriod.model.BasicUser;
import com.example.prifscourseandriod.model.Chat;
import com.example.prifscourseandriod.model.FoodOrder;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private FoodOrder currentOrder;
    private Chat currentChat;
    private List<SimpleReview> messages = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ListView messageList;
    private EditText messageBody;
    private Button sendButton;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean creatingChat = false;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        messageList = findViewById(R.id.messageList);
        messageBody = findViewById(R.id.messageField);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(this::sendMessage);

        String orderJson = getIntent().getStringExtra("orderJsonObject");
        if (orderJson != null) {
            Gson gson = new Gson();
            currentOrder = gson.fromJson(orderJson, FoodOrder.class);
        }

        if (currentOrder != null && currentOrder.getChat() != null) {
            currentChat = currentOrder.getChat();
        }


        if (currentChat == null || currentChat.getId() == 0) {
            Toast.makeText(this, "Chat not available for this order", Toast.LENGTH_SHORT).show();
        }

         loadChatMessages();
    }
    private void loadChatMessages() {
        if (currentChat == null || currentChat.getId() == 0) {
            Toast.makeText(this, "Chat ID is null or invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                String url = String.format(GET_MESSAGES_BY_CHAT_ID, currentChat.getId());
                String response = RestOperations.sendGet(url);
                if (response == null || response.trim().isEmpty()) return;

                Gson gson = new Gson();
                response = response.trim();
                if (response.startsWith("[[") && response.endsWith("]]")) {
                    response = response.substring(1, response.length() - 1);
                }

                SimpleReview[] chatMessages;
                if (response.startsWith("[")) {
                    chatMessages = gson.fromJson(response, SimpleReview[].class);
                } else if (response.startsWith("{")) {
                    chatMessages = new SimpleReview[]{gson.fromJson(response, SimpleReview.class)};
                } else {
                    return;
                }

                messages.clear();
                messages.addAll(Arrays.asList(chatMessages));
                Set<Integer> userIds = new HashSet<>();
                for (SimpleReview msg : messages) userIds.add(msg.getCommentOwnerId());
                Map<Integer, String> userNames = new HashMap<>();

                for (int userId : userIds) {
                    String userResponse = RestOperations.sendGet(String.format(Constants.GET_USER_BY_ID, userId));
                    if (userResponse != null && !userResponse.isEmpty()) {
                        BasicUser user = gson.fromJson(userResponse, BasicUser.class);
                        userNames.put(userId, user.getName()); // or getFullName()
                    } else {
                        userNames.put(userId, "User " + userId);
                    }
                }
                List<String> display = new ArrayList<>();
                for (SimpleReview msg : messages) {
                    String name = userNames.getOrDefault(msg.getCommentOwnerId(), "User " + msg.getCommentOwnerId());
                    display.add(name + ": " + msg.getReviewText());
                }

                handler.post(() -> {
                    if (adapter == null) {
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
                        messageList.setAdapter(adapter);
                    } else {
                        adapter.clear();
                        adapter.addAll(display);
                        adapter.notifyDataSetChanged();
                    }
                    messageList.smoothScrollToPosition(messages.size() - 1);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading messages", e);
                handler.post(() -> Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private BasicUser getConnectedUser() throws IOException {
        String url = String.format(Constants.GET_USER_BY_ID, currentOrder.getBuyer().getId());
        String response = RestOperations.sendGet(url);

        if (response == null || response.trim().isEmpty()) {
            Log.e(TAG, "getConnectedUser: empty response for userId " + currentOrder.getBuyer().getId());
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(response, BasicUser.class);
    }



    private void sendMessage(View view) {
        String text = messageBody.getText().toString().trim();
        if (text.isEmpty() || currentChat == null) return;

        messageBody.setText("");

        executor.execute(() -> {
            try {
                BasicUser currentUser = getConnectedUser();

                if (currentUser == null) {
                    handler.post(() ->
                            Toast.makeText(ChatActivity.this, "Cannot get current user", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                if (currentChat.getId() == 0 || currentUser.getId() == 0) {
                    handler.post(() ->
                            Toast.makeText(ChatActivity.this, "Invalid chat or user ID", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                SimpleReview newMessage = new SimpleReview(
                        LocalDate.now().toString(),
                        text,
                        currentUser.getId(),
                        currentChat.getId()
                );

                Gson gson = new Gson();
                String jsonBody = gson.toJson(newMessage);

                // Breadcrumb: show exactly what will be sent
                Log.d(TAG, "=== JSON to send ===");
                Log.d(TAG, jsonBody);
                Log.d(TAG, "===================");

                String url = String.format(Constants.SEND_MESSAGE_TO_CHAT, currentChat.getId());
                String response = RestOperations.sendPost(url, jsonBody);

                Log.d(TAG, "Sent message response: " + response);

                loadChatMessages();

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() ->
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }





    private void updateMessageList() {
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
            messageList.setAdapter(adapter);
        }

        List<String> display = new ArrayList<>();
        for (SimpleReview r : messages) {
            display.add(r.getCommentOwnerId() + ": " + r.getReviewText());
        }

        adapter.clear();
        adapter.addAll(display);
        adapter.notifyDataSetChanged();

        messageList.smoothScrollToPosition(messages.size() - 1);
    }


}
