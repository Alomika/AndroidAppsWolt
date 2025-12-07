package com.example.prifscourseandriod;

import static com.example.prifscourseandriod.Constants.VALIDATE_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.messageList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    public void validateUser(View view) {
        TextView login = findViewById(R.id.loginField);
        TextView password = findViewById(R.id.passwordField);

        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("login", login.getText().toString());
        data.addProperty("password", password.getText().toString());
        String info = gson.toJson(data);
        System.out.println(gson);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(VALIDATE_USER_URL, info);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, WoltMain.class);
                        intent.putExtra("userJsonObject", response);
                        startActivity(intent);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void openRegistrationActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}