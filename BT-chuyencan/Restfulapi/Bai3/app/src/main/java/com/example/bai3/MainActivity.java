package com.example.bai3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText edtName, edtEmail;
    private TextView txtResponse;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        txtResponse = findViewById(R.id.txtResponse);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                new PostUserTask().execute(name, email);
            }
        });
    }

    private class PostUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/users");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Tạo JSON Object
                JSONObject json0bject = new JSONObject();
                json0bject.put( "name", params[0]);
                json0bject.put( "email", params[1]);

                // Gửi dữ liệu
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(json0bject.toString().getBytes());
                os.flush();
                os.close();

                // Nhận phản hồi từ server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String Line;
                StringBuilder response = new StringBuilder();
                while ((Line = reader. readLine ()) != null) {
                    response.append(Line);
                }
                reader.close();

                connection.disconnect();
                result = response.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            txtResponse.setText("Phản hồi từ server: \n" + s);
        }
    }
}