package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieCursorAdapter.class.getSimpleName();
    private int mResourceId;
    private OnMovieClickedListener mListener = null;

    public MovieCursorAdapter(Context context, Cursor cursor, int resource, OnMovieClickedListener listener) {
        super(context, cursor);
        mResourceId = resource;
        mListener = listener;
    }

    public interface OnMovieClickedListener {
        public void onClick(long movieId);
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView mImageView = null;
        View mRootView = null;
        View mLoadView = null;
        Target mTarget = null;
        long mMovieId = -1;

        OnItemClickListener mListener = null;
        public static int itemCount = 1;

        public interface OnItemClickListener {
            public void onClick(long position);
        }

        public ViewHolder(View rootView,
                          ImageView imageView, View loadView, Target target, OnItemClickListener listener) {
            super(rootView);
            mImageView = imageView;
            mRootView = rootView;
            mLoadView = loadView;
            mTarget = target;
            mListener = listener;
            itemCount = (++itemCount) % 4;
            switch (itemCount) {
                case 0:
                    rootView.setBackground(new ColorDrawable(Color.WHITE));
                    break;
                case 1:
                    rootView.setBackground(new ColorDrawable(Color.GRAY));
                    break;
                case 2:
                    rootView.setBackground(new ColorDrawable(Color.GREEN));
                    break;
                case 3:
                    rootView.setBackground(new ColorDrawable(Color.CYAN));
                    break;
            }
            imageView.setBackgroundColor(0x55555555);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(mMovieId);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View v = LayoutInflater.from(context).inflate(mResourceId, parent, false);
        final ImageView imageView = (ImageView) v.findViewById(R.id.list_poster_image);

//        imageView.setMinimumWidth(size);
        imageView.setVisibility(View.VISIBLE);
        final View loader = (View) v.findViewById(R.id.list_poster_image_progress);
        loader.bringToFront();
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        Log.d(LOG_TAG, "Bitmap loaded. size: " + bitmap.getWidth() + "," + bitmap.getHeight());
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);

//                        v.requestLayout();
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(LOG_TAG, "Failed to load image!");
                //               imageView.setBackground(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                imageView.setBackground(placeHolderDrawable);
            }
        };

        ViewHolder holder = new ViewHolder(v, imageView, loader, target, new ViewHolder.OnItemClickListener() {
            @Override
            public void onClick(long position) {
                mListener.onClick(position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final String posterPath = cursor.getString(MovieListFragment.COL_MOVIE_POSTER_PATH);

        viewHolder.mMovieId = cursor.getLong(MovieListFragment.COL_MOVIE_MOVIE_ID);
        viewHolder.mImageView.setVisibility(View.VISIBLE);
        viewHolder.mImageView.setBackgroundColor(Color.BLUE);
/*        holder.mLoadView.setVisibility(View.GONE);
        holder.mImageView.setVisibility(View.GONE);
        holder.mLoadView.setVisibility(View.VISIBLE);
*/
        final ImageView iv = viewHolder.mImageView;
        final Target t = viewHolder.mTarget;

        // Start loading the poster image.
        final MoviePosterLoader posterLoader =
                new MoviePosterLoader(viewHolder.mImageView.getContext(), posterPath);
        iv.post(new Runnable() {
            @Override
            public void run() {
                posterLoader.loadMoviePoster(t);
            }
        });
    }
}