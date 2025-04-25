package com.example.bai7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Button btnPing;
    private TextView txtPingResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnPing = findViewById(R.id.btnPing);
        txtPingResult = findViewById(R.id.txtPingResult);

        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurePing();
            }
        });
    }
    private void measurePing() {
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("ping -c 1 google.com");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("Ping", line);
                    result.append(line).append("\n");
                }

                int exitCode = process.waitFor();

                runOnUiThread(() -> {
                    if (exitCode == 0) {
                        txtPingResult.setText("Kết quả:\n" + result.toString());
                    } else {
                        txtPingResult.setText("Ping thất bại (exit code: " + exitCode + ")");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> txtPingResult.setText("Lỗi: " + e.getMessage()));
            }
        }).start();
    }
}