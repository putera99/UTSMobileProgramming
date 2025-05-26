package com.example.utsweatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // API Key dari weatherapi.com
    private static final String API_KEY = "15ccb9120d754a1b948123940252405";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    private Spinner spinnerCity;
    private TextView tvCityName, tvTemperature, tvCondition, tvHumidity, tvWindSpeed, tvFeelsLike,
            tvPressure, tvVisibility, tvUvIndex, tvLastUpdated;
    private ImageView ivWeatherIcon;
    private RecyclerView rvForecast;
    private LinearLayout llCurrentWeather;
    private ProgressBar progressBar;

    private RequestQueue requestQueue;
    private ForecastAdapter forecastAdapter;
    private List<ForecastItem> forecastList;

    private String[] cities = {"Jakarta", "Bandung", "Surabaya", "Bali", "Yogyakarta"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinner();
        setupRecyclerView();

        requestQueue = Volley.newRequestQueue(this);

        showLoading(true);
        getCurrentWeather(cities[0]);
        getForecast(cities[0]);
    }

    private void initViews() {
        spinnerCity = findViewById(R.id.spinnerCity);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        tvPressure = findViewById(R.id.tvPressure);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvUvIndex = findViewById(R.id.tvUvIndex);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        rvForecast = findViewById(R.id.rvForecast);
        llCurrentWeather = findViewById(R.id.llCurrentWeather);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities); // Ini adalah item yang ditampilkan saat spinner tertutup

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Ini adalah item untuk daftar dropdown
        spinnerCity.setAdapter(adapter);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cities[position];
                showLoading(true);
                getCurrentWeather(selectedCity);
                getForecast(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        forecastList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastList);
        rvForecast.setLayoutManager(new LinearLayoutManager(this));
        rvForecast.setAdapter(forecastAdapter);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            llCurrentWeather.setVisibility(View.GONE);
            rvForecast.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            llCurrentWeather.setVisibility(View.VISIBLE);
            rvForecast.setVisibility(View.VISIBLE);
        }
    }

    private void getCurrentWeather(String city) {
        String url = BASE_URL + "current.json?key=" + API_KEY + "&q=" + city + "&aqi=no";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject location = response.getJSONObject("location");
                            JSONObject current = response.getJSONObject("current");
                            JSONObject condition = current.getJSONObject("condition");

                            tvCityName.setText(location.getString("name") + ", " +
                                    location.getString("country"));
                            tvTemperature.setText(Math.round(current.getDouble("temp_c")) + "°C");
                            tvCondition.setText(condition.getString("text"));
                            tvHumidity.setText("Humidity: " + current.getInt("humidity") + "%");
                            tvWindSpeed.setText("Wind: " + current.getDouble("wind_kph") + " km/h " +
                                    current.getString("wind_dir"));
                            tvFeelsLike.setText("Feels like: " +
                                    Math.round(current.getDouble("feelslike_c")) + "°C");
                            tvPressure.setText("Pressure: " + current.getDouble("pressure_mb") + " mb");
                            tvVisibility.setText("Visibility: " + current.getDouble("vis_km") + " km");
                            tvUvIndex.setText("UV Index: " + current.getDouble("uv"));
                            tvLastUpdated.setText("Last updated: " + current.getString("last_updated"));

                            String iconUrl = "https:" + condition.getString("icon");
                            Picasso.get().load(iconUrl).into(ivWeatherIcon);

                            // showLoading(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Error parsing weather data");
                            showLoading(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showError("Error loading weather data");
                        showLoading(false);
                    }
                });

        requestQueue.add(request);
    }

    private void getForecast(String city) {
        String url = BASE_URL + "forecast.json?key=" + API_KEY + "&q=" + city + "&days=5&aqi=no&alerts=no";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject forecast = response.getJSONObject("forecast");
                            JSONArray forecastDays = forecast.getJSONArray("forecastday");

                            forecastList.clear();

                            for (int i = 0; i < forecastDays.length(); i++) {
                                JSONObject day = forecastDays.getJSONObject(i);
                                JSONObject dayInfo = day.getJSONObject("day");
                                JSONObject condition = dayInfo.getJSONObject("condition");

                                String date = day.getString("date");
                                String dayName = getDayName(date);
                                double maxTemp = dayInfo.getDouble("maxtemp_c");
                                double minTemp = dayInfo.getDouble("mintemp_c");
                                String conditionText = condition.getString("text");
                                String iconUrl = "https:" + condition.getString("icon");

                                ForecastItem item = new ForecastItem(dayName, maxTemp, minTemp,
                                        conditionText, iconUrl);
                                forecastList.add(item);
                            }

                            forecastAdapter.notifyDataSetChanged();
                            showLoading(false); // Panggil di sini karena ini adalah request terakhir.

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Error parsing forecast data");
                            showLoading(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showError("Error loading forecast data");
                        showLoading(false);
                    }
                });

        requestQueue.add(request);
    }

    private String getDayName(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Inner class for Forecast Item
    public static class ForecastItem {
        private String day;
        private double maxTemp;
        private double minTemp;
        private String condition;
        private String iconUrl;

        public ForecastItem(String day, double maxTemp, double minTemp, String condition, String iconUrl) {
            this.day = day;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.condition = condition;
            this.iconUrl = iconUrl;
        }

        // Getters
        public String getDay() { return day; }
        public double getMaxTemp() { return maxTemp; }
        public double getMinTemp() { return minTemp; }
        public String getCondition() { return condition; }
        public String getIconUrl() { return iconUrl; }
    }
}