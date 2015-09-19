package edu.spbau.android.forecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.spbau.android.forecast.WeatherContract.WeatherEntry;

public class WeatherDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather.db";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
            WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL," +
            WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
            WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +
            WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
            WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL," +
            WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL," +
            WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                "UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
    }

}
