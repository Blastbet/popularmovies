package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ilkka on 3.11.2016.
 */

public class TrailerCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = TrailerCursorAdapter.class.getSimpleName();

    public TrailerCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public TrailerCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public TrailerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.v(LOG_TAG, "New view... " + cursor.toString());
        return inflater.inflate(R.layout.trailer_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final String name = cursor.getString(MovieDetailsFragment.COL_TRAILER_NAME);
        final String key = cursor.getString(MovieDetailsFragment.COL_TRAILER_KEY);
        final String site = cursor.getString(MovieDetailsFragment.COL_TRAILER_SITE);

        Log.v(LOG_TAG, "Binding " + name + " on site: " + site + " (" + key + ")");
        TextView trailerName = (TextView) view.findViewById(R.id.text_trailer_name);
        trailerName.setText(name);
        Uri.Builder uriBuilder = new Uri.Builder();
        final Uri trailerUri = uriBuilder.scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
                context.startActivity(trailerIntent);
                Log.v(LOG_TAG, "Started activity to view: " + trailerUri);
            }
        });
    }
}
