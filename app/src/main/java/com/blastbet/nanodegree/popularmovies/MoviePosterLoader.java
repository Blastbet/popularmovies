package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by ilkka on 30.4.2016.
 */
public class MoviePosterLoader {

    private static final String LOG_TAG = MoviePosterLoader.class.getSimpleName();

    private static final String TMDB_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String API_KEY_PARAM = "api_key";

    private Context mContext;

    private String mPosterPath;

    private Target mTarget;

    MoviePosterLoader(Context context, Movie movie) {
        mContext = context;
        mPosterPath = movie.getPosterPath();
    }

    MoviePosterLoader(Context context, String posterPath) {
        mContext = context;
        mPosterPath = posterPath;
    }

    private Uri getURI() {
        final String sizeKey = mContext.getString(R.string.pref_poster_size_key);
        final String sizeDefault = mContext.getString(R.string.pref_poster_size_default);
        final String size = PreferenceManager.getDefaultSharedPreferences(mContext).getString(sizeKey, sizeDefault);

        StringBuilder sb = new StringBuilder(TMDB_BASE_IMAGE_URL);
        sb.append('w');
        sb.append(size);
        sb.append('/');
        sb.append(mPosterPath);
        return Uri.parse(sb.toString()).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                .build();
    }

    public void loadMoviePoster(final ImageView view, final Callback callback) {
        final Uri uri = getURI();
        Picasso.with(mContext)
                .load(uri)
                .fit()
                .into(view, callback);
    }

    public void loadMoviePoster(Target target) {
        mTarget = target;
        final Uri uri = getURI();
        Picasso.with(mContext)
                .load(uri)
                .into(target);
    }

    public void loadMoviePoster(int width, int height, Target target) {
        mTarget = target;
        Picasso.with(mContext)
                .load(getURI())
                .resize(width, height)
                .into(target);
    }
}
