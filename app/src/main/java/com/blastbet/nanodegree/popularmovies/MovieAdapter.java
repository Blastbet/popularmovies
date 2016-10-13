package com.blastbet.nanodegree.popularmovies;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.List;

public class MovieAdapter extends CursorRecyclerViewAdapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private int mResourceId;
//    private List<Movie> mMovies = null;
    private OnMovieClickedListener mListener = null;

    public MovieAdapter(int resource, List<Movie> objects, OnMovieClickedListener listener) {
        super();
        mResourceId = resource;
        mMovies = objects;
        mListener = listener;
    }


    public interface OnMovieClickedListener {
        public void onClick(Movie movie);
        public void onClick(int movieId);
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView mImageView = null;
        View mRootView = null;
        View mLoadView = null;
        Target mTarget = null;
        int mMovieId = -1;

        OnItemClickListener mListener = null;
        public static int itemCount = 1;

        public interface OnItemClickListener {
            public void onClick(int position);
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
            switch(itemCount) {
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

        final View v = LayoutInflater.from(parent.getContext()).inflate(mResourceId, parent, false);

        final ImageView imageView = (ImageView) v.findViewById(R.id.list_poster_image);
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
            public void onClick(int position) {
                mListener.onClick(mMovies.get(position));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final String posterPath = cursor.getString(MovieListFragment.COL_MOVIE_POSTER_PATH);
        final int movieId =

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

//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        final Movie movie = mMovies.get(position);
//
//        holder.mImageView.setVisibility(View.VISIBLE);
//        holder.mImageView.setBackgroundColor(Color.BLUE);
///*        holder.mLoadView.setVisibility(View.GONE);
//        holder.mImageView.setVisibility(View.GONE);
//        holder.mLoadView.setVisibility(View.VISIBLE);
//*/
//        final ImageView iv = holder.mImageView;
//        final Target t = holder.mTarget;
//        // Start loading the poster image.
//        final MoviePosterLoader posterLoader = new MoviePosterLoader(holder.mImageView.getContext(), movie);
//        iv.post(new Runnable() {
//            @Override
//            public void run() {
//                posterLoader.loadMoviePoster(t);
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
        {
            return 0;
        }
        return mMovies.size();
    }

    private int findMovieFromDataset(final Movie movie) {
        for (int i = 0; i < mMovies.size(); i++) {
            if (mMovies.get(i).equals(movie)) {
                return i;
            }
        }
        return -1;
    }

    public void setNewMovies(List<Movie> newMovies) {
        if (newMovies != null)
        {
            this.mMovies = newMovies;
            this.notifyDataSetChanged();
        }
    }
}



/*
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

import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

*/
/**
 * Created by ilkka on 30.3.2016.
 *//*

public class MovieAdapter extends BaseAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private int mResourceId;
    private List<Movie> mMovies = null;

    public MovieAdapter(Context context, int resource, List<Movie> objects) {
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
        return mMovies.get(position);
    }

    @Override
    public int getCount() {
        if (mMovies == null)
        {
            return 0;
        }
        return mMovies.size();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (mMovies == null || mMovies.size() <= position) {
            return convertView;
        }

        ImageHolder holder = null;

        final Movie movie = mMovies.get(position);

        if (convertView != null ) {
            try {
                holder = (ImageHolder) convertView.getTag();
            } catch (ClassCastException e) {
                holder = null;
            }
        } else if (holder == null) {
            holder = new ImageHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(mResourceId, parent, false);

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.list_poster_image);
            final View loader = convertView.findViewById(R.id.list_poster_image_progress);

            holder.imageView = imageView;
            holder.loader = loader;

            */
/** Target for picasso, that handles showing "loading" animation when images are being
             *  downloaded.
             *//*

            holder.target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    loader.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
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

            convertView.setTag(holder);
        }

        holder.imageView.setVisibility(View.GONE);
        holder.loader.setVisibility(View.VISIBLE);

        */
/** Scale the images to fit evenly on and fill the whole screen *//*

        holder.imageView.setVisibility(View.GONE);
        holder.loader.setVisibility(View.VISIBLE);
        int columnWidth = ((GridView)parent).getColumnWidth();

        // Start loading the poster image.
        MoviePosterLoader posterLoader = new MoviePosterLoader(mContext, movie);
        posterLoader.loadMoviePoster(columnWidth, 0, holder.target);

        return convertView;
    }

    public void setNewMovies(List<Movie> newMovies) {
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
*/
