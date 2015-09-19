package edu.spbau.android.forecast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationChangedReceiver extends BroadcastReceiver {

    private static final String TAG = LocationChangedReceiver.class.getSimpleName();

    public LocationChangedReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String locationKey = LocationManager.KEY_LOCATION_CHANGED;

        if (intent.hasExtra(locationKey)) {
            Location location = (Location) intent.getExtras().get(locationKey);

            Intent startService = new Intent(context, ForecastUpdateService.class);
            startService.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
            context.startService(startService);
        }
    }
}
