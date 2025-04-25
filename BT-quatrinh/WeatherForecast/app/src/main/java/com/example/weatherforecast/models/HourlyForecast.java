package com.example.weatherforecast.models;

public class HourlyForecast {
    public String hour;
    public String iconUrl;
    public String temperature;

    public HourlyForecast(String hour, String iconUrl, String temperature) {
        this.hour = hour;
        this.iconUrl = iconUrl;
        this.temperature = temperature;
    }
}
