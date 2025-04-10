package com.example.currencyconverter;

import java.util.Collections;
import java.util.Iterator;
import android.graphics.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String fromCurrency = "USD"; // Giá trị mặc định
    private String toCurrency = "VND";   // Giá trị mặc định
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Spinner fromCurrencySpinner, toCurrencySpinner;
    private EditText amountEditText;
    private TextView resultTextView;
    private Button convertButton;
    private RecyclerView historyRecyclerView;
    private ArrayList<HistoryItem> historyList;
    private HistoryAdapter historyAdapter;
    private SharedPrefManager sharedPrefManager;
    private LineChart exchangeRateChart;
    private List<ExchangeRateHistory> rateHistoryList = new ArrayList<>();
    private Handler chartUpdateHandler = new Handler();
    private Runnable chartUpdateRunnable;
    private static final long CHART_UPDATE_INTERVAL = 300000; // 5 phút
    private String[] currencyCodes;
    private String[] currencyNames;
    private String apiKey = "34f0aa6df07e9dfcfd45bc6d"; // Thay bằng API key của bạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo view
        initViews();
        initChart();
        // Lấy dữ liệu từ strings.xml
        currencyCodes = getResources().getStringArray(R.array.currency_codes);
        currencyNames = getResources().getStringArray(R.array.currency_names);

        // Thiết lập spinner
        setupSpinners();

        // Khởi tạo lịch sử
        sharedPrefManager = new SharedPrefManager(this);
        historyList = sharedPrefManager.getHistory();
        setupHistoryRecyclerView();

        // Xử lý sự kiện convert
        convertButton.setOnClickListener(v -> convertCurrency());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        chartUpdateHandler.removeCallbacks(chartUpdateRunnable); // Dừng cập nhật biểu đồ
    }

    private void initViews() {
        fromCurrencySpinner = findViewById(R.id.from_currency_spinner);
        toCurrencySpinner = findViewById(R.id.to_currency_spinner);
        amountEditText = findViewById(R.id.amount_edit_text);
        resultTextView = findViewById(R.id.result_text_view);
        convertButton = findViewById(R.id.convert_button);
        historyRecyclerView = findViewById(R.id.history_recycler_view);
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencyNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        // Mặc định USD -> VND
        fromCurrencySpinner.setSelection(adapter.getPosition("United States Dollar (USD)"));
        toCurrencySpinner.setSelection(adapter.getPosition("Vietnamese Dong (VND)"));
    }

    private void setupHistoryRecyclerView() {
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(historyList);
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void handleConversionResult(String result, String fromCurrency, String toCurrency, double amount) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("result").equals("success")) {
                    double conversionResult = jsonObject.getDouble("conversion_result");
                    double conversionRate = jsonObject.getDouble("conversion_rate");

                    // Hiển thị kết quả
                    String resultText = String.format(Locale.getDefault(),
                            "%.2f %s = %.2f %s\nTỷ giá: 1 %s = %.6f %s",
                            amount, fromCurrency,
                            conversionResult, toCurrency,
                            fromCurrency, conversionRate, toCurrency);

                    resultTextView.setText(resultText);

                    // Lưu vào lịch sử
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String dateTime = sdf.format(new Date());

                    HistoryItem historyItem = new HistoryItem(
                            dateTime,
                            amount + " " + fromCurrency,
                            conversionResult + " " + toCurrency,
                            conversionRate
                    );

                    historyList.add(0, historyItem);
                    sharedPrefManager.saveHistory(historyList);
                    historyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Lỗi khi lấy tỷ giá: " + jsonObject.getString("error-type"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
        }
    }

    private void convertCurrency() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int fromPos = fromCurrencySpinner.getSelectedItemPosition();
        int toPos = toCurrencySpinner.getSelectedItemPosition();

        if (fromPos == toPos) {
            Toast.makeText(this, "Hai loại tiền tệ phải khác nhau", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromCurrency = currencyCodes[fromPos];
        String toCurrency = currencyCodes[toPos];

        // Thêm dòng này để cập nhật biểu đồ khi chọn loại tiền mới
        chartUpdateHandler.removeCallbacks(chartUpdateRunnable);
        rateHistoryList.clear();
        fetchHistoricalRates(fromCurrency, toCurrency);

        // Thực hiện chuyển đổi
        executorService.execute(() -> {
            try {
                String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" +
                        fromCurrency + "/" + toCurrency + "/" + amount;

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

                String result = stringBuilder.toString();
                mainHandler.post(() -> handleConversionResult(result, fromCurrency, toCurrency, amount));
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(() ->
                        Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void initChart() {
        exchangeRateChart = findViewById(R.id.exchangeRateChart);

        // Cấu hình biểu đồ
        exchangeRateChart.getDescription().setEnabled(false);
        exchangeRateChart.setTouchEnabled(true);
        exchangeRateChart.setDragEnabled(true);
        exchangeRateChart.setScaleEnabled(true);
        exchangeRateChart.setPinchZoom(true);

        // Cấu hình trục X
        XAxis xAxis = exchangeRateChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        // Cấu hình trục Y bên trái
        exchangeRateChart.getAxisLeft().setEnabled(true);
        exchangeRateChart.getAxisRight().setEnabled(false);
    }

    private void updateChartData() {
        List<Entry> entries = new ArrayList<>();

        // Chuyển đổi dữ liệu sang định dạng cho biểu đồ
        for (ExchangeRateHistory history : rateHistoryList) {
            entries.add(new Entry(history.getDate().getTime(), (float) history.getRate()));
        }

        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, "Tỷ giá " + fromCurrency + "/" + toCurrency);
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.RED);
            dataSet.setLineWidth(2f);
            dataSet.setCircleColor(Color.RED);
            dataSet.setCircleRadius(4f);
            dataSet.setDrawCircleHole(false);
            dataSet.setValueTextSize(10f);

            LineData lineData = new LineData(dataSet);
            exchangeRateChart.setData(lineData);

            // Cấu hình trục X để hiển thị ngày tháng
            XAxis xAxis = exchangeRateChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

                @Override
                public String getFormattedValue(float value) {
                    return mFormat.format(new Date((long) value));
                }
            });

            exchangeRateChart.invalidate(); // Refresh biểu đồ
        } else {
            exchangeRateChart.clear();
            exchangeRateChart.setNoDataText("Không có dữ liệu tỷ giá");
        }
    }

    private void fetchHistoricalRates(String fromCurrency, String toCurrency) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                // Sử dụng endpoint history với khoảng thời gian 7 ngày
                Calendar calendar = Calendar.getInstance();
                String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                String apiUrl = "https://api.frankfurter.app/" + startDate + ".." + endDate +
                        "?from=" + fromCurrency + "&to=" + toCurrency;

                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    String result = stringBuilder.toString();
                    mainHandler.post(() -> processHistoricalRates(result, fromCurrency, toCurrency));
                } else {
                    mainHandler.post(() -> {
                        Toast.makeText(MainActivity.this,
                                "Lỗi API: " + responseCode, Toast.LENGTH_SHORT).show();
                        generateMockData(); // Dùng dữ liệu giả nếu API fail
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                    generateMockData(); // Dùng dữ liệu giả
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void processHistoricalRates(String result, String fromCurrency, String toCurrency) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject rates = jsonObject.getJSONObject("rates");

            rateHistoryList.clear();

            // Lặp qua các ngày trong response
            Iterator<String> dateIterator = rates.keys();
            while (dateIterator.hasNext()) {
                String dateStr = dateIterator.next();
                JSONObject dailyRate = rates.getJSONObject(dateStr);
                double rate = dailyRate.getDouble(toCurrency);

                // Chuyển đổi ngày từ String sang Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(dateStr);

                rateHistoryList.add(new ExchangeRateHistory(date, rate));
            }

            // Sắp xếp theo thứ tự thời gian tăng dần
            Collections.sort(rateHistoryList, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

            updateChartData();
        } catch (Exception e) {
            e.printStackTrace();
            generateMockData(); // Dùng dữ liệu giả nếu có lỗi xử lý
        }
    }

    private void generateMockData() {
        rateHistoryList.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7); // 7 ngày trước

        // Tạo dữ liệu giả với tỷ giá dao động quanh 23,000 VND/USD
        double baseRate = 23000;

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            // Tạo tỷ giá ngẫu nhiên ±500 so với baseRate
            double rate = baseRate + (Math.random() * 1000 - 500);
            rateHistoryList.add(new ExchangeRateHistory(calendar.getTime(), rate));
        }

        updateChartData();
        Toast.makeText(this, "Đang sử dụng dữ liệu mẫu", Toast.LENGTH_SHORT).show();
    }

    private void startChartAutoUpdate(String fromCurrency, String toCurrency) {
        chartUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLatestRateForChart(fromCurrency, toCurrency);
                chartUpdateHandler.postDelayed(this, CHART_UPDATE_INTERVAL);
            }
        };
        chartUpdateHandler.postDelayed(chartUpdateRunnable, CHART_UPDATE_INTERVAL);
    }

    private void fetchLatestRateForChart(String fromCurrency, String toCurrency) {
        executorService.execute(() -> {
            try {
                String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey +
                        "/pair/" + fromCurrency + "/" + toCurrency;

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

                String result = stringBuilder.toString();
                mainHandler.post(() -> processLatestRateForChart(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void processLatestRateForChart(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("result").equals("success")) {
                double rate = jsonObject.getDouble("conversion_rate");
                rateHistoryList.add(new ExchangeRateHistory(new Date(), rate));

                // Giữ chỉ 24 điểm dữ liệu
                if (rateHistoryList.size() > 24) {
                    rateHistoryList.remove(0);
                }

                updateChartData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class FetchExchangeRateTask extends AsyncTask<String, Void, String> {
        private String fromCurrency;
        private String toCurrency;
        private double amount;

        @Override
        protected String doInBackground(String... params) {
            fromCurrency = params[0];
            toCurrency = params[1];
            amount = Double.parseDouble(params[2]);

            try {
                String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" +
                        fromCurrency + "/" + toCurrency + "/" + amount;

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

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("success")) {
                        double conversionResult = jsonObject.getDouble("conversion_result");
                        double conversionRate = jsonObject.getDouble("conversion_rate");

                        // Hiển thị kết quả
                        String resultText = String.format(Locale.getDefault(),
                                "%.2f %s = %.2f %s\nTỷ giá: 1 %s = %.6f %s",
                                amount, fromCurrency,
                                conversionResult, toCurrency,
                                fromCurrency, conversionRate, toCurrency);

                        resultTextView.setText(resultText);

                        // Lưu vào lịch sử
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String dateTime = sdf.format(new Date());

                        HistoryItem historyItem = new HistoryItem(
                                dateTime,
                                amount + " " + fromCurrency,
                                conversionResult + " " + toCurrency,
                                conversionRate
                        );

                        historyList.add(0, historyItem);
                        sharedPrefManager.saveHistory(historyList);
                        historyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Lỗi khi lấy tỷ giá: " + jsonObject.getString("error-type"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}