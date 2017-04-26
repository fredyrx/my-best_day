package com.framos.mybestday.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.framos.mybestday.BestDayApp;
import com.framos.mybestday.R;
import com.framos.mybestday.models.Coord;
import com.framos.mybestday.models.WeatherResponse;
import com.framos.mybestday.network.WeatherApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Coord currentPosition = new Coord();

    CardView weatherCardView;
    TextView nameTextView;
    TextView tempMaxTextView;
    TextView tempMinTextView;
    TextView pressureTextView;
    TextView humidityTextView;
    TextView tempTextView;
    TextView countryTextView;

    private GoogleApiClient mGoogleApiClient;
    Location lastLocation;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Example
        currentPosition.setLat(0.0);
        currentPosition.setLon(0.0);

        paintWeather();

        setCurrentLocation();
    }

    private void paintWeather() {
        AndroidNetworking.get(WeatherApi.API_URL)
                .addQueryParameter("lat", currentPosition.getLat().toString())
                .addQueryParameter("lon", currentPosition.getLon().toString())
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
                        tempMaxTextView.setText(String.format("%.2f%s",
                                convertKelvinToCelcious(weather.getMain().getTemp_max()),
                                getString(R.string.temperature_unit)));
                        tempMinTextView.setText(String.format("%.2f%s",
                                convertKelvinToCelcious(weather.getMain().getTemp_min()),
                                getString(R.string.temperature_unit)));
                        tempTextView.setText(String.format("%.2f%s",
                                convertKelvinToCelcious(weather.getMain().getTemp()),
                                getString(R.string.temperature_unit)));
                        pressureTextView.setText(String.format("%s%s", weather.getMain().getPressure(),
                                getString(R.string.pressure_unit)));
                        humidityTextView.setText(String.format("%s%s", weather.getMain().getHumidity(),
                                getString(R.string.humidity_unit)));
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("BEST_DAY_API", anError.toString());
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            }

            return;
        }
        setCurrentLocation();
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation != null) {
            currentPosition.setLat(lastLocation.getLatitude());
            currentPosition.setLon(lastLocation.getLongitude());
            paintWeather();
            Toast.makeText(this, String.format("lat:%s, lon:%s", currentPosition.getLat(), currentPosition.getLon()), Toast.LENGTH_LONG).show();
        } else {
            Log.d("PERMISION", "no location permission granted");
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200) {
            if ((grantResults.length == 1)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                setCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Double convertKelvinToCelcious(Double kelvin){
        return kelvin - 273.15;
    }
}
