package com.example.bai5;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView txtResult;
    private Button btnCheckSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtResult = findViewById(R.id.txtResult);
        btnCheckSpeed = findViewById(R.id.btnCheckSpeed);

        btnCheckSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkSpeed();
            }
        });
    }
    private void checkNetworkSpeed() {
        // Dùng luồng riêng để tránh NetworkOnMainThreadException
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                long startTime = System.currentTimeMillis();

                Request request = new Request.Builder()
                        .url("https://www.google.com")
                        .build();

                Response response = client.newCall(request).execute();

                long endTime = System.currentTimeMillis();
                long speed = endTime - startTime;

                String result = "Kết quả: " + speed + " ms";

                runOnUiThread(() -> txtResult.setText(result));

                response.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> txtResult.setText("Lỗi khi kiểm tra: " + e.getMessage()));
            }
        }).start();
    }
}