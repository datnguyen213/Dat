package com.example.currencyconverter;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyApiService {
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private String apiKey;

    public CurrencyApiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONObject getExchangeRate(String fromCurrency, String toCurrency, double amount) {
        try {
            String apiUrl = BASE_URL + apiKey + "/pair/" + fromCurrency + "/" + toCurrency + "/" + amount;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}