package edu.spbau.android.forecast;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_list_item_view,
                parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        long date = cursor.getLong(ForecastFragment.COLUMN_WEATHER_DATE);
        holder.date.setText(Utility.getFriendlyDayString(date));

        double day = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_DAY);
        double nigh = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_NIGHT);
        holder.temp.setText(Utility.getFormattedTemperature(day, nigh));

        double speed = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_WIND_SPEED);
        double degrees = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_WIND_DEGREES);
        holder.wind.setText(Utility.getFormattedWind(speed, degrees));

        int weatherId = cursor.getInt(ForecastFragment.COLUMN_WEATHER_ID);
        holder.icon.setImageResource(Utility.getWeatherConditionIcon(weatherId));
    }

    public static class ViewHolder {
        public TextView date;
        public TextView temp;
        public TextView wind;
        public ImageView icon;

        public ViewHolder(View view) {
            date = (TextView) view.findViewById(R.id.date);
            temp = (TextView) view.findViewById(R.id.temp);
            wind = (TextView) view.findViewById(R.id.wind);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

}
