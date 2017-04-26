package com.framos.mybestday.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.framos.mybestday.BestDayApp;
import com.framos.mybestday.R;
import com.framos.mybestday.models.Coord;
import com.framos.mybestday.models.WeatherResponse;
import com.framos.mybestday.network.WeatherApi;
import com.google.gson.Gson;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    Coord currentPosition;

    CardView weatherCardView;
    TextView nameTextView;
    TextView tempMaxTextView;
    TextView tempMinTextView;
    TextView pressureTextView;
    TextView humidityTextView;
    TextView tempTextView;
    TextView countryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weatherCardView = (CardView) findViewById(R.id.wearher_card_view);
        countryTextView = (TextView) findViewById(R.id.country_textview);
        nameTextView = (TextView) findViewById(R.id.name_textview);
        tempMaxTextView = (TextView) findViewById(R.id.temp_max_textview);
        tempMinTextView = (TextView) findViewById(R.id.temp_min_textview);
        tempTextView = (TextView) findViewById(R.id.temp_textview);
        pressureTextView = (TextView) findViewById(R.id.pressure_textview);
        humidityTextView = (TextView) findViewById(R.id.humidity_textview);

        // set aplpha
        weatherCardView.setAlpha((float) 0.7);

        // GET GPS POSITION}

        currentPosition = new Coord();
        currentPosition.setLat(35);
        currentPosition.setLon(139);

        AndroidNetworking.get(WeatherApi.API_URL)
                .addQueryParameter("lat", currentPosition.getLat().toString())
                .addQueryParameter("lon", currentPosition.getLon().toString() )
                .addQueryParameter("appid", getString(R.string.weather_api_key))
                //.addQueryParameter("apiKey",getString(R.string.news_api_key))
                .setTag(HomeActivity.class.getSimpleName())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        // Almacenamos el resultado en memoria
                        WeatherResponse weather = gson.fromJson(response.toString(), WeatherResponse.class);
                        BestDayApp.getInstance().setWeatherResponse(weather);

                        // Seteamos los valores en el layout
                        nameTextView.setText(weather.getName());
                        countryTextView.setText(weather.getSys().getCountry());
                        tempMaxTextView.setText( String.format("%s%s",weather.getMain().getTemp_max(), getString(R.string.temperature_unit)) );
                        tempMinTextView.setText(String.format("%s%s",weather.getMain().getTemp_min(), getString(R.string.temperature_unit)) );
                        tempTextView.setText(String.format("%s%s",weather.getMain().getTemp(), getString(R.string.temperature_unit)));
                        pressureTextView.setText(String.format("%s%s",weather.getMain().getPressure(), getString(R.string.pressure_unit)));
                        humidityTextView.setText(String.format("%s%s",weather.getMain().getHumidity(), getString(R.string.humidity_unit)));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("BEST_DAY_API",anError.toString());
                        Log.d("BEST_DAY_API", anError.getLocalizedMessage());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
