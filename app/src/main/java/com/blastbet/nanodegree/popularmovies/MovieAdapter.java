package com.blastbet.nanodegree.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ilkka on 30.3.2016.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private int mResourceId;
    private Movie[] mMovies = null;

    public MovieAdapter(Context context, int resource, Movie[] objects) {
        super();
        mContext = context;
        mResourceId = resource;
        mMovies = objects;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        if (mMovies == null)
        {
            return 0;
        }
        return mMovies.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (mMovies == null || mMovies.length <= position) {
            return convertView;
        }

        final Movie movie = mMovies[position];

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);

            convertView.setClickable(true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Clicked on movie \"" + movie.getName() + "\"", Toast.LENGTH_SHORT).show();
                }
            });
        }
        TextView textView = (TextView) convertView.findViewById(R.id.list_poster_title);
        textView.setText(movie.getName());

        return convertView;
    }

    public void setNewMovies(Movie... newMovies) {
        if (newMovies != null)
        {
            this.mMovies = newMovies;
            this.notifyDataSetChanged();
        }
    }
}
