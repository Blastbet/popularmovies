package com.blastbet.nanodegree.popularmovies.tmdb;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import retrofit2.Response;

/**
 * Created by ilkka on 14.10.2016.
 */

public class MovieDetailsLoader extends AsyncTaskLoader<Response> {

    public MovieDetailsLoader(Context context) {
        super(context);
    }

    @Override
    public Response loadInBackground() {
        return null;
    }

    @Override
    public void deliverResult(Response data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    public void onCanceled(Response data) {
        super.onCanceled(data);
    }

    private void releaseResources(Response data) {

    }
}
