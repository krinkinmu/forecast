package edu.spbau.android.forecast;

import java.text.SimpleDateFormat;

public class Utility {

    public static String getFriendlyDayString(long dateInMillis) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(dateInMillis);
    }

    public static String getFormattedTemperature(double day, double night) {
        return String.format("%.1f/%.1f \u2103", day, night);
    }

    public static int getWeatherConditionIcon(int weather) {
        if (weather >= 200 && weather <= 232) {
            return R.mipmap.thunder;
        }
        if (weather >= 300 && weather <= 321 || weather >= 500 && weather <= 531) {
            return R.mipmap.rain;
        }
        if (weather >= 600 && weather <= 622) {
            return R.mipmap.snow;
        }
        if (weather >= 701 && weather <= 721 || weather == 741) {
            return R.mipmap.mist;
        }
        if (weather == 800) {
            return R.mipmap.sun;
        }
        if (weather == 801 || weather == 802) {
            return R.mipmap.partly_sunny;
        }
        if (weather == 803 || weather == 804) {
            return R.mipmap.cloud;
        }
        if (weather >= 761 && weather <= 781 || weather >= 900 && weather <= 902
                || weather >= 957 && weather <= 962 || weather == 905) {
            return R.mipmap.storm;
        }
        if (weather == 906) {
            return R.mipmap.hail;
        }
        return -1;
    }

    public static String getFormattedWind(double speed, double degrees) {
        String direction;
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else {
            direction = "NW";
        }
        return String.format("%.2f kph %s", speed, direction);
    }

}
