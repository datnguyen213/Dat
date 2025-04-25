package com.example.weatherforecast.api;

import com.example.weatherforecast.models.ForecastResponse;
import com.example.weatherforecast.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("forecast/hourly")
    Call<ForecastResponse> getHourlyForecast(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );
    @GET("weather")
    Call<WeatherResponse> getWeatherByCoordinates(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );
}
