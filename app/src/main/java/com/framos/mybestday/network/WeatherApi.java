package com.framos.mybestday.network;

import com.framos.mybestday.models.Coord;
import com.framos.mybestday.models.WeatherResponse;

/**
 * Created by sistemas on 25/04/2017.
 */

public class WeatherApi {
    public static String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static String IMAGE_URL = "http://openweathermap.org/img/w/{imageName}.png";

    private WeatherResponse weather;
    private Coord currentPosition;

    public Coord getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coord currentPosition) {
        this.currentPosition = currentPosition;
    }

    public WeatherResponse getWeather() {
        return weather;
    }

    public void setWeather(WeatherResponse weather) {
        this.weather = weather;
    }
}
