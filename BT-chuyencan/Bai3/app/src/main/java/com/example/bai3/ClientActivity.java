package com.example.bai3;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    private TextView chatBox;
    private EditText messageInput;
    private Button sendButton;
    private PrintWriter out;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatBox = findViewById(R.id.chatBox);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        new Thread(new ClientThread()).start();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.1.6", 12345); // Đổi thanh IP của Server
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString();
        if (out != null && !message.isEmpty()) {
            new Thread(() -> {
                out.println(message);
                handler.post(() -> {
                    chatBox.append("\nBan: " + message);
                    messageInput.setText("");
                });
            }).start();
        }
    }

}