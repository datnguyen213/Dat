package com.example.weatherforecast.models;

import java.util.List;

public class WeatherResponse { // current weather
    private Main main;
    private List<Weather> weather;
    private String name;
    private Wind wind;

    public Main getMain() { return main; }
    public List<Weather> getWeather() { return weather; }
    public String getName() { return name; }
    public Wind getWind() { return wind; }

    public class Main {
        private float temp;
        private float feels_like;
        private int humidity;

        public float getTemp() { return temp; }
        public float getFeelsLike() { return feels_like; }
        public int getHumidity() { return humidity; }
    }

    public class Weather {
        private String description;
        private String icon;

        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public class Wind {
        private String speed;
        private String deg;

        public String getSpeed() { return speed; }
        public String getDeg() { return deg; }
    }

    public float getTempInFahrenheit() {
        return (getMain().getTemp() * 9/5) + 32;
    }
}