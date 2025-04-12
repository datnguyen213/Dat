package com.example.server;

import android.os.Bundle;
import android.widget.TextView;

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
    private TCPServerThread serverThread;
//    private TextView textViewMessages;
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
//        textViewMessages = findViewById(R.id.textViewMessages);


        serverThread = new TCPServerThread(this);
        serverThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverThread.stopServer();
    }

    public void updateMessages(final String message) {
        runOnUiThread(() -> {
            // Append tin nhắn mới vào TextView
//            textViewMessages.append(message + "\n");
            messageAdapter.addMessage(message);  // Thêm tin nhắn mới vào RecyclerView
        });
    }
}
