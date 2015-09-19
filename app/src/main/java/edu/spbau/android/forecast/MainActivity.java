package edu.spbau.android.forecast;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private LocationManager mLocationManager;
    private PendingIntent mLocationChangedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CoordinatesFragment())
                    .commit();
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Intent intent = new Intent(this, LocationChangedReceiver.class);
        mLocationChangedIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5 * 60 * 1000, 0, mLocationChangedIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(mLocationChangedIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
