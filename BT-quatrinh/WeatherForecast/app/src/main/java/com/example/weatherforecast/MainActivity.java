package com.example.weatherforecast;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.adapter.DailyForecastAdapter;
import com.example.weatherforecast.adapter.HourlyAdapter;
import com.example.weatherforecast.viewModel.WeatherViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.TileOverlay;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView tvLocation, tvTemperature, tvDescription, tvHumidity, tvWindSpeed;
    private ImageView imgWeatherIcon;
    private WeatherViewModel weatherViewModel;
    private HourlyAdapter hourlyAdapter;
    private RecyclerView recyclerHourly, recyclerDaily;
    private DailyForecastAdapter dailyAdapter;
    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;
    private MapView mapView;
    private TileOverlay currentOverlay;
    private Spinner layerSpinner;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private final String API_KEY = "29e3bb81a6bd3f60828663b4c66e030e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ View thời tiết
        tvLocation = findViewById(R.id.tv_location);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvDescription = findViewById(R.id.tv_description);
        tvHumidity = findViewById(R.id.tv_humidity);
        tvWindSpeed = findViewById(R.id.tv_wind_speed);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);

        weatherViewModel = new WeatherViewModel();

        // Permission location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        // Quan sát dữ liệu thời tiết
        weatherViewModel.getWeather().observe(this, weatherResponse -> {
            if (weatherResponse != null) {
                tvLocation.setText(weatherResponse.getName());
                tvTemperature.setText(weatherResponse.getMain().getTemp() + "°C");
                tvDescription.setText(weatherResponse.getWeather().get(0).getDescription());
                tvHumidity.setText("Độ ẩm: " + weatherResponse.getMain().getHumidity() + "%");
                tvWindSpeed.setText("Gió: " + weatherResponse.getWind().getSpeed() + " m/s");

                String iconCode = weatherResponse.getWeather().get(0).getIcon();
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Picasso.get().load(iconUrl).into(imgWeatherIcon);
            } else {
                Toast.makeText(this, "Không thể lấy dữ liệu thời tiết.", Toast.LENGTH_SHORT).show();
            }
        });

        // RecyclerView - Hourly
        recyclerHourly = findViewById(R.id.recyclerHourly);
        recyclerHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyAdapter = new HourlyAdapter(new ArrayList<>());
        recyclerHourly.setAdapter(hourlyAdapter);

        weatherViewModel.getHourlyForecast().observe(this, hourlyAdapter::setData);

        // RecyclerView - Daily
        recyclerDaily = findViewById(R.id.recyclerDaily);
        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));
        dailyAdapter = new DailyForecastAdapter(new ArrayList<>());
        recyclerDaily.setAdapter(dailyAdapter);

        weatherViewModel.getDailyForecast().observe(this, dailyAdapter::setData);

        // MapView
        mapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Spinner layer chọn kiểu bản đồ thời tiết
        layerSpinner = findViewById(R.id.layerSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weather_layers_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        layerSpinner.setAdapter(adapter);

        layerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LOC", "Spinner selected position: " + position);

                String[] layerValues = getResources().getStringArray(R.array.weather_layers_values);
                String layerType = layerValues[position]; // ví dụ "Clouds_new"
                Log.d("LOC", "onItemSelected: " + layerType);

                setWeatherLayer(layerType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("LOC", "onNothingSelected");
            }
        });

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy vị trí
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latitude = 10.7769;
                longitude = 106.7009;

                weatherViewModel.fetchWeatherByCoordinates(latitude, longitude);
                weatherViewModel.fetchHourlyForecast(latitude, longitude);

                if (mMap != null) {
                    LatLng loc = new LatLng(latitude, longitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(loc).title("Vị trí hiện tại"));
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10f));
                }
            } else {
                Toast.makeText(this, "Không lấy được vị trí.", Toast.LENGTH_SHORT).show();
            }
        });
    }
//private void getCurrentLocation() {
//    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
//
//    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//            // KHÔNG đè toạ độ cố định ở đây nữa!
//
//            weatherViewModel.fetchWeatherByCoordinates(latitude, longitude);
//            weatherViewModel.fetchHourlyForecast(latitude, longitude);
//
//            if (mMap != null) {
//                LatLng loc = new LatLng(latitude, longitude);
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(loc).title("Vị trí hiện tại"));
//                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10f));
//            }
//        } else {
//            // Nếu không có last location, yêu cầu lấy location mới
//            requestNewLocationData();
//        }
//    });
//}
//
//    private void requestNewLocationData() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setNumUpdates(1); // Chỉ lấy 1 location mới
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                Location location = locationResult.getLastLocation();
//                if (location != null) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//
//                    weatherViewModel.fetchWeatherByCoordinates(latitude, longitude);
//                    weatherViewModel.fetchHourlyForecast(latitude, longitude);
//
//                    if (mMap != null) {
//                        LatLng loc = new LatLng(latitude, longitude);
//                        mMap.clear();
//                        mMap.addMarker(new MarkerOptions().position(loc).title("Vị trí hiện tại"));
//                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10f));
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Không thể lấy vị trí mới.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, Looper.getMainLooper());
//    }


    private void setWeatherLayer(String layerType) {
        if (currentOverlay != null) {
            Log.d("LOC", "Removing current overlay...");
            currentOverlay.remove();
        }

        // Log lại layerType khi nó thay đổi
        Log.d("LOC", "Setting weather layer: " + layerType);

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String url = String.format(Locale.US,
                        "https://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=%s",
                        layerType, zoom, x, y, API_KEY);
                // Log URL để kiểm tra xem nó đúng không
                Log.d("LOC", "Generated tile URL: " + url);
                try {
                    return new URL(url);
                } catch (MalformedURLException e) {
                    Log.e("LOC", "Malformed URL: " + e.getMessage());
                    return null;
                }
            }
        };

        // Kiểm tra lại xem mMap có hợp lệ không
        if (mMap != null) {
            Log.d("LOC", "Adding tile overlay to map...");
            currentOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).transparency(0.3f));
        } else {
            Log.e("LOC", "mMap is null. Cannot add tile overlay.");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Cho phép zoom bằng nút + -
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Cho phép zoom bằng tay (pinch)
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Di chuyển đến vị trí mặc định (VD: HCMC)
        LatLng defaultLatLng = new LatLng(10.762622, 106.660172); // SGU nè :D
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 14.0f));
        setWeatherLayer("clouds_new");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Các lifecycle cho mapView
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onPause() { mapView.onPause(); super.onPause(); }
    @Override protected void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
    @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}