package edu.spbau.android.forecast;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForecastUpdateService extends IntentService {

    private final static String TAG = ForecastUpdateService.class.getSimpleName();

    public ForecastUpdateService() {
        super(TAG);
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
        String forecastString = null;
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

            forecastString = buffer.toString();
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

        Log.d(TAG, forecastString);
    }

}
