package com.blastbet.nanodegree.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ilkka on 3.11.2016.
 */

public class TrailerCursorAdapter extends CursorRecyclerViewAdapter<TrailerCursorAdapter.ViewHolder> {

    Context mContext;

    public TrailerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView textViewTrailerName;

        Uri trailerURI;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            textViewTrailerName = (TextView) rootView.findViewById(R.id.text_trailer_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View v = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        viewHolder.textViewTrailerName.setText(cursor.getString(MovieDetailsFragment.COL_TRAILER_NAME));
        Uri.Builder uriBuilder = new Uri.Builder();
        viewHolder.trailerURI = uriBuilder.scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", cursor.getString(MovieDetailsFragment.COL_TRAILER_KEY))
                .build();
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, viewHolder.trailerURI);
                view.getContext().startActivity(trailerIntent);
            }
        });
    }
}