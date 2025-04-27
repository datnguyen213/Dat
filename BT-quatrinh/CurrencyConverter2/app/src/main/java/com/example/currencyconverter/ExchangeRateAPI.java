package com.example.currencyconverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ExchangeRateAPI {

    private static final String API_KEY = "34f0aa6df07e9dfcfd45bc6d"; // Replace with your valid API key
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    // Fetch latest exchange rate for a specific currency pair
    public static String getExchangeRatePair(String baseCurrency, String targetCurrency) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = BASE_URL + API_KEY + "/pair/" + baseCurrency + "/" + targetCurrency;
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "{\"result\":\"error\",\"error-type\":\"HTTP " + responseCode + "\"}";
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":\"error\",\"error-type\":\"" + e.getMessage() + "\"}";
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Fetch latest exchange rates for all currencies
    public static String getExchangeRates(String baseCurrency) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(BASE_URL + API_KEY + "/latest/" + baseCurrency);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "{\"result\":\"error\",\"error-type\":\"HTTP " + responseCode + "\"}";
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"result\":\"error\",\"error-type\":\"" + e.getMessage() + "\"}";
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}