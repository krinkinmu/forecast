package edu.spbau.android.forecast;

import java.text.SimpleDateFormat;

public class Utility {

    public static String getFriendlyDayString(long dateInMillis) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(dateInMillis);
    }

    static String getFormattedTemperature(double temperature) {
        return Double.toString(temperature);
    }

}
