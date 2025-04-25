package com.example.bai1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetworkUtils {
    private static final String API_URL = "https://restcountries.com/v3.1/all";
    public static ArrayList<Country> fetchCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String Line;
                while ((Line = reader.readLine()) != null) {
                    response.append(Line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response. toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject countryJson = jsonArray.getJSONObject(i);
                    String name = countryJson.getJSONObject("name").getString("common");
                    String capital = countryJson.has("capital") ? countryJson.getJSONArray("capital").getString(0) : "N/A";
                    String region = countryJson.getString("region");
                    countries.add(new Country(name, capital, region));
                }
            }

        } catch (Exception e) {
            Log.e( "NetworkUtils", "Error fetching API", e);
        }
        return countries;
    }
}
