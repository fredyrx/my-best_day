package com.framos.mybestday.network;

import com.framos.mybestday.models.WeatherResponse;

/**
 * Created by sistemas on 25/04/2017.
 */

public class WeatherApi {
    public static String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private WeatherResponse weather;

    public WeatherResponse getWeather() {
        return weather;
    }

    public void setWeather(WeatherResponse weather) {
        this.weather = weather;
    }
}
