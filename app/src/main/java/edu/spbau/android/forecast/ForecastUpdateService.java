package edu.spbau.android.forecast;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ForecastUpdateService extends IntentService {

    private final static String TAG = ForecastUpdateService.class.getSimpleName();

    public ForecastUpdateService() {
        super(TAG);
    }

    private void parseJsonData(String json) {
        final String JSON_LIST = "list";
        final String JSON_PRESSURE = "pressure";
        final String JSON_HUMIDITY = "humidity";
        final String JSON_SPEED = "speed";
        final String JSON_DEGREE = "deg";
        final String JSON_TEMPERATURE = "temp";
        final String JSON_MAX_TEMPERATURE = "max";
        final String JSON_MIN_TEMPERATURE = "min";

        try {
            JSONObject forecastJson = new JSONObject(json);
            JSONArray weatherArray = forecastJson.getJSONArray(JSON_LIST);

            ArrayList<ContentValues> forecast = new ArrayList<>(weatherArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime = dayTime.setJulianDay(julianStartDay + i);

                double pressure = dayForecast.getDouble(JSON_PRESSURE);
                int humidity = dayForecast.getInt(JSON_HUMIDITY);
                double speed = dayForecast.getDouble(JSON_SPEED);
                double degree = dayForecast.getDouble(JSON_DEGREE);

                JSONObject temperatureObject = dayForecast.getJSONObject(JSON_TEMPERATURE);
                double high = temperatureObject.getDouble(JSON_MAX_TEMPERATURE);
                double low = temperatureObject.getDouble(JSON_MIN_TEMPERATURE);

                ContentValues weatherValues = new ContentValues();
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, degree);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, speed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                forecast.add(weatherValues);
            }

            if (forecast.size() > 0) {
                ContentValues values[] = new ContentValues[forecast.size()];
                forecast.toArray(values);
                getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, values);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing JSON", e);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String locationKey = LocationManager.KEY_LOCATION_CHANGED;

        final String FORECAST_BASE_URL =
                "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String LATITUDE_PARAM = "lat";
        final String LONGITUDE_PARAM = "lon";
        final String MODE_PARAM = "mode";
        final String JSON = "json";
        final String UNITS_PARAM = "units";
        final String METRIC = "metric";

        if (!intent.hasExtra(locationKey)) {
            Log.w(TAG, "Received intent without location");
            return;
        }

        Location location = (Location) intent.getExtras().get(locationKey);
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(LATITUDE_PARAM, Double.toString(location.getLatitude()))
                    .appendQueryParameter(LONGITUDE_PARAM, Double.toString(location.getLongitude()))
                    .appendQueryParameter(MODE_PARAM, JSON)
                    .appendQueryParameter(UNITS_PARAM, METRIC).build();
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }

            parseJsonData(buffer.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing stream", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
