package com.blastbet.nanodegree.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mMovies[position];
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
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (mMovies == null || mMovies.length <= position) {
            return convertView;
        }

        ImageHolder holder = null;

        final Movie movie = mMovies[position];

        if (convertView == null) {
            holder = new ImageHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.list_poster_image);
            final View loader = convertView.findViewById(R.id.list_poster_image_progress);

            holder.imageView = imageView;
            holder.loader = loader;

            /** Target for picasso, that handles showing "loading" animation when images are being
             *  downloaded.
             */
            holder.target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    loader.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    //               imageView.setBackground(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    imageView.setBackground(placeHolderDrawable);
                }
            };

            convertView.setTag(holder);

        } else {
            holder = (ImageHolder) convertView.getTag();
        }

        holder.imageView.setVisibility(View.GONE);
        holder.loader.setVisibility(View.VISIBLE);

        /** Scale the images to fit evenly on and fill the whole screen */
        int columnWidth = ((GridView)parent).getColumnWidth();
        Picasso.with(mContext)
                .load(movie.getPosterImage())
                .resize(columnWidth,0)
                .into(holder.target);

        return convertView;
    }

    public void setNewMovies(Movie... newMovies) {
        if (newMovies != null)
        {
            this.mMovies = newMovies;
            this.notifyDataSetChanged();
        }
    }

    static class ImageHolder {
        ImageView imageView = null;
        View loader = null;
        Target target = null;
    }
}
