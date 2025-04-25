package com.example.bai5;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText edtCurrency;
    private TextView txtResult;
    private Button btnConvert;
    private static final String API_KEY = "6860846fd4edeab8c1cdb3a9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtCurrency = findViewById(R.id.edtCurrency);
        txtResult = findViewById(R.id.txtResult);
        btnConvert = findViewById(R.id.btnConvert);

        btnConvert.setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                String currency = edtCurrency.getText().toString().toUpperCase();
                new ConvertCurrencyTask().execute(currency);
            }
        });
    }

    private class ConvertCurrencyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return "Lỗi: " + e.getMessage();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject json0bject = new JSONObject(s);
                JSONObject rates = json0bject.getJSONObject("conversion_rates");
                double rate = rates.getDouble(edtCurrency.getText().toString().toUpperCase());
                txtResult.setText("1 USD = " + rate + " " + edtCurrency.getText().toString().toUpperCase());
            } catch (Exception e) {
                txtResult.setText("Không tìm thấy tỷ giá");
            }
        }
    }
}