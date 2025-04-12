package com.example.bai3;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.BufferedReader;
import java.io. InputStreamReader;
import java.io.PrintWriter;
import java.net. ServerSocket;
import java.net. Socket;

public class ServerActivity extends AppCompatActivity {
    private TextView chatBox;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);
        new Thread(new ServerThread()).start();
    }

    class ServerThread implements Runnable {
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(12345);
                Socket client = serverSocket.accept(); // Chờ Client kết nối
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                while (true) {
                    final String message = in.readLine(); // Đọc tin nhắn từ Client
                    handler.post(() -> chatBox.append("\nClient: " + message));
                    if (message.equalsIgnoreCase("exit")) break; // Thoát nếu nhận "exit"
                }

                client.close();
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}