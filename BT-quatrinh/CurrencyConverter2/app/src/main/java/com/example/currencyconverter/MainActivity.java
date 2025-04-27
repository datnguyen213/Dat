package com.example.currencyconverter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Spinner fromCurrencySpinner, toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView;
    private Button convertButton;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<ConversionHistory> historyList;
    private CurrencyDatabaseHelper dbHelper;
    private LineChart currencyChart;

    private Map<String, Double> exchangeRates = new HashMap<>();
    private String[] currencies = {"USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "VND"};
    private String fromCurrency = "USD";
    private String toCurrency = "EUR";

    // For real-time chart updates
    private List<Entry> rateEntries = new ArrayList<>();
    private List<String> timeLabels = new ArrayList<>();
    private static final int MAX_DATA_POINTS = 10; // Keep the last 10 data points
    private Handler handler;
    private Runnable fetchRatesRunnable;
    private static final long UPDATE_INTERVAL = 30 * 1000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new CurrencyDatabaseHelper(this);

        // Initialize UI components
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        amountEditText = findViewById(R.id.amountEditText);
        resultTextView = findViewById(R.id.resultTextView);
        convertButton = findViewById(R.id.convertButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        currencyChart = findViewById(R.id.currencyChart);
        setupChart();

        // Setup spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        // Spinner listeners
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrency = currencies[position];
                fetchExchangeRates();
                startRealTimeUpdates(); // Restart real-time updates with new currency pair
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrency = currencies[position];
                updateResult();
                startRealTimeUpdates(); // Restart real-time updates with new currency pair
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Convert button listener
        convertButton.setOnClickListener(v -> convertCurrency());

        // Setup history RecyclerView
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);

        // Load history from database
        loadHistory();

        // Fetch initial exchange rates
        fetchExchangeRates();

        // Start real-time updates
        handler = new Handler(Looper.getMainLooper());
        startRealTimeUpdates();
    }

    private void fetchExchangeRates() {
        new FetchExchangeRatesTask().execute(fromCurrency);
    }

    private void convertCurrency() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (exchangeRates.containsKey(toCurrency)) {
                double rate = exchangeRates.get(toCurrency);
                double result = amount * rate;

                resultTextView.setText(String.format("%.2f %s = %.2f %s",
                        amount, fromCurrency, result, toCurrency));

                // Save to history
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date());
                ConversionHistory history = new ConversionHistory(
                        date, fromCurrency, toCurrency, amount, result, rate);

                dbHelper.addConversion(history);
                historyList.add(0, history);
                historyAdapter.notifyItemInserted(0);
            } else {
                Toast.makeText(this, "Không thể lấy tỷ giá", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateResult() {
        String amountStr = amountEditText.getText().toString();
        if (!amountStr.isEmpty() && exchangeRates.containsKey(toCurrency)) {
            try {
                double amount = Double.parseDouble(amountStr);
                double rate = exchangeRates.get(toCurrency);
                double result = amount * rate;
                resultTextView.setText(String.format("%.2f %s = %.2f %s",
                        amount, fromCurrency, result, toCurrency));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    }

    private void setupChart() {
        currencyChart.getDescription().setEnabled(false);
        currencyChart.setTouchEnabled(true);
        currencyChart.setDragEnabled(true);
        currencyChart.setScaleEnabled(true);
        currencyChart.setPinchZoom(true);
        currencyChart.setDrawGridBackground(false);

        // Configure X-axis
        XAxis xAxis = currencyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabels));

        // Configure Y-axis
        YAxis yAxis = currencyChart.getAxisLeft();
        yAxis.setGranularity(0.1f);
        currencyChart.getAxisRight().setEnabled(false);
    }

    private void startRealTimeUpdates() {
        // Stop any existing updates
        if (fetchRatesRunnable != null) {
            handler.removeCallbacks(fetchRatesRunnable);
        }

        // Clear previous data
        rateEntries.clear();
        timeLabels.clear();
        updateChart();

        // Define the runnable to fetch rates
        fetchRatesRunnable = new Runnable() {
            @Override
            public void run() {
                new FetchRealTimeRatesTask().execute(fromCurrency, toCurrency);
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        // Start the updates
        handler.post(fetchRatesRunnable);
    }

    private class FetchRealTimeRatesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String base = params[0];
            String target = params[1];
            return ExchangeRateAPI.getExchangeRatePair(base, target);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                if (json.getString("result").equals("error")) {
                    Toast.makeText(MainActivity.this, "Error fetching real-time data: " + json.getString("error-type"), Toast.LENGTH_SHORT).show();
                    return;
                }

                double rate = json.getDouble("conversion_rate");

                // Add the new rate with timestamp
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                timeLabels.add(time);
                rateEntries.add(new Entry(rateEntries.size(), (float) rate));

                // Keep only the last MAX_DATA_POINTS
                if (rateEntries.size() > MAX_DATA_POINTS) {
                    rateEntries.remove(0);
                    timeLabels.remove(0);
                    // Adjust x-values of remaining entries
                    for (int i = 0; i < rateEntries.size(); i++) {
                        rateEntries.get(i).setX(i);
                    }
                }

                updateChart();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error parsing real-time data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateChart() {
        if (rateEntries.isEmpty()) {
            currencyChart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(rateEntries, "Tỷ giá " + fromCurrency + " sang " + toCurrency);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(dataSet);
        currencyChart.setData(lineData);

        // Update x-axis labels
        XAxis xAxis = currencyChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabels));

        currencyChart.invalidate(); // Refresh chart
    }

    private void loadHistory() {
        historyList.clear();
        historyList.addAll(dbHelper.getAllConversions());
        historyAdapter.notifyDataSetChanged();
    }

    private class FetchExchangeRatesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String baseCurrency = params[0];
            return ExchangeRateAPI.getExchangeRates(baseCurrency);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("result").equals("success")) {
                    JSONObject rates = jsonObject.getJSONObject("conversion_rates");
                    exchangeRates.clear();

                    for (String currency : currencies) {
                        if (rates.has(currency)) {
                            exchangeRates.put(currency, rates.getDouble(currency));
                        }
                    }

                    updateResult();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Không thể lấy tỷ giá: " + jsonObject.getString("error-type"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this,
                        "Lỗi phân tích dữ liệu tỷ giá: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop real-time updates when the activity is paused
        if (fetchRatesRunnable != null) {
            handler.removeCallbacks(fetchRatesRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume real-time updates
        startRealTimeUpdates();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        // Stop real-time updates
        if (fetchRatesRunnable != null) {
            handler.removeCallbacks(fetchRatesRunnable);
        }
        super.onDestroy();
    }
}