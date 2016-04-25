package com.blastbet.nanodegree.popularmovies;

import android.net.Uri;
import android.os.Parcelable;

/**
 * Created by ilkka on 6.4.2016.
 */
public class MoviePoster extends Parcelable {

    protected String imageName;
    protected final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private final String API_KEY = "api_key";

    public Uri getUri() {
        return Uri.parse(IMAGE_BASE_URL + imageName).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.THEMOVIEDB_API_KEY)
                .build();
    }


}
