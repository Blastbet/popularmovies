package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ilkka on 3.11.2016.
 */

public class TrailerCursorAdapter extends CursorAdapter {
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
        return inflater.inflate(R.layout.trailer_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String name = cursor.getString(MovieDetailsFragment.COL_TRAILER_NAME);
        final String key  = cursor.getString(MovieDetailsFragment.COL_TRAILER_KEY);
        final String site = cursor.getString(MovieDetailsFragment.COL_TRAILER_SITE);

        TextView trailerName = (TextView) view.findViewById(R.id.text_trailer_name);
        trailerName.setText(name);
    }
}
