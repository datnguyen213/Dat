package com.example.client;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<String> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString();
        String serverIp = "192.168.1.6";
        new TCPClientTask(MainActivity.this, serverIp).execute(message);
    }

    public void updateMessages(final String message) {
        runOnUiThread(() -> {
            messageAdapter.addMessage(message);  // Thêm tin nhắn mới vào RecyclerView
//            Toast.makeText(MainActivity.this, "Phản hồi từ server: " + message, Toast.LENGTH_LONG).show();
        });
    }
}
