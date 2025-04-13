package com.example.bai3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> titles = new ArrayList<>();
    private ExecutorService executorService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        // Khởi tạo ExecutorService và Handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Gọi hàm Lấy dữ liệu RSS
        fetchRSS("https://vnexpress.net/rss/tin-moi-nhat.rss");
    }

    private void fetchRSS(String urlString) {
        executorService.execute(() -> {
            ArrayList<String> fetchedTitles = new ArrayList<>();
            try {
                Log.d("RSS", "Fetching from URL: " + urlString); // Kiểm tra URL
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                // Kiểm tra phản hồi HTTP
                Log.d("RSS", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream()) {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setInput(inputStream, "UTF-8");

                        boolean insideItem = false;
                        String title = "";
                        int eventType = parser.getEventType();

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {
                                if ("item".equalsIgnoreCase(parser.getName())) {
                                    insideItem = true;
                                } else if (insideItem && "title".equalsIgnoreCase(parser.getName())) {
                                    title = parser.nextText();
                                    Log.d("RSS", "Fetched Title: " + title);
                                }
                            } else if (eventType == XmlPullParser.END_TAG && "item".equalsIgnoreCase(parser.getName())) {
                                fetchedTitles.add(title);
                                insideItem = false;
                            }
                            eventType = parser.next();
                        }
                    }
                } else {
                    Log.e("RSS", "HTTP Error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("RSS", "Error fetching RSS: " + e.getMessage());
            }

            handler.post(() -> {
                if (!fetchedTitles.isEmpty()) {
                    titles.clear();
                    titles.addAll(fetchedTitles);
                    adapter.notifyDataSetChanged();
                } else {
                    // Nếu không có dữ liệu
                    Log.e("RSS", "No data fetched! Check network & RSS format.");
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Đóng ExecutorService để giải phóng tài nguyên
    }
}