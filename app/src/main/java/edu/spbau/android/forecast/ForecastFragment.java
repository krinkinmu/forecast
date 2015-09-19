package edu.spbau.android.forecast;

import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    public static final int COLUMN_WEATHER_DATE = 1;
    public static final int COLUMN_WEATHER_LOW = 2;
    public static final int COLUMN_WEATHER_HIGH = 3;

    private ForecastAdapter mForecastAdapter;

    public ForecastFragment() { }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ListView mListView = (ListView) root.findViewById(R.id.forecast_list_view);
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        mListView.setAdapter(mForecastAdapter);
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherUri = WeatherContract.WeatherEntry.CONTENT_URI;

        return new CursorLoader(getActivity(), weatherUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

}
