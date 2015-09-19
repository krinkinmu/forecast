package edu.spbau.android.forecast;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_DATE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = WeatherContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", WEATHER_WITH_DATE);
        return matcher;
    }

    private static void normalizeDate(ContentValues values) {
        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
            long date = WeatherContract.normalizeDate(
                    values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE));
            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, date);
        }
    }

    private WeatherDBHelper mDbHelper;

    private Cursor getWeather(String[] projection, String selection, String[] selectionArgs,
                              String sortOrder)
    {
        return mDbHelper.getReadableDatabase().query(WeatherContract.WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getWetherByDate(Uri uri, String[] projection, String sortOrder) {
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        String selection = WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";
        String selectionArgs[] = new String[] { Long.toString(date) };

        return mDbHelper.getReadableDatabase().query(WeatherContract.WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                cursor = getWeather(projection, selection, selectionArgs, sortOrder);
                break;
            case WEATHER_WITH_DATE:
                cursor = getWetherByDate(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                normalizeDate(values);
                long date = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                long id = mDbHelper.getWritableDatabase().insert(
                        WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return WeatherContract.WeatherEntry.buildWeatherUri(date);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                int count = mDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                int count = mDbHelper.getWritableDatabase().update(
                        WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case WEATHER:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                int count = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
