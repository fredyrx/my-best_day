package com.framos.mybestday;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.framos.mybestday.models.WeatherResponse;
import com.framos.mybestday.network.WeatherApi;

/**
 * Created by sistemas on 25/04/2017.
 */

public class BestDayApp extends Application {

    WeatherApi api = new WeatherApi();
    private static BestDayApp instance;

    public BestDayApp(){
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }

    public static BestDayApp getInstance(){
        return instance;
    }

    public WeatherResponse getWeather(){
        return api.getWeather();
    }

    public void setWeatherResponse(WeatherResponse w){
        api.setWeather(w);
    }
}
