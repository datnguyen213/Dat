package com.example.currencyconverter;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefManager {
    private static final String PREF_NAME = "CurrencyConverterPref";
    private static final String HISTORY_KEY = "conversion_history";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveHistory(ArrayList<HistoryItem> historyList) {
        String json = gson.toJson(historyList);
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply();
    }

    public ArrayList<HistoryItem> getHistory() {
        String json = sharedPreferences.getString(HISTORY_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<HistoryItem>>() {}.getType();
        return gson.fromJson(json, type);
    }
}