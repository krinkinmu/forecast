package edu.spbau.android.forecast;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
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

        double low = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_LOW);
        holder.low.setText(Utility.getFormattedTemperature(low));

        double high = cursor.getDouble(ForecastFragment.COLUMN_WEATHER_HIGH);
        holder.high.setText(Utility.getFormattedTemperature(high));
    }

    public static class ViewHolder {
        public TextView date;
        public TextView low;
        public TextView high;

        public ViewHolder(View view) {
            date = (TextView) view.findViewById(R.id.date);
            low = (TextView) view.findViewById(R.id.low);
            high = (TextView) view.findViewById(R.id.high);
        }
    }

}
