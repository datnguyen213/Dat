package com.example.weatherforecast.models;

public class DailyForecast {
    public String date;          // Ngày: 2025-04-17
    public String iconUrl;      // Icon đại diện trong ngày (lấy buổi trưa hoặc giờ giữa)
    public String tempMin;      // Nhiệt độ thấp nhất
    public String tempMax;
//    public String description;// Nhiệt độ cao nhất

    public DailyForecast(String date, String iconUrl, String tempMin, String tempMax) {
        this.date = date;
        this.iconUrl = iconUrl;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
//        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

}
