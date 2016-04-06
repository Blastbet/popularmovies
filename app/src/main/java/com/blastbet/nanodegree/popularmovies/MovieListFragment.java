package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MovieListFragment extends Fragment {

    private MovieAdapter mAdapter = null;
    private MovieFetchTask mTask = null;

    private final String IMAGE_PATH = "http://image.tmdb.org/t/p/w185/";
    private String mSortKey = null;

    private MovieListCallback mCallback;

    public interface MovieListCallback {
        void onMovieSelectedListener(Movie movie);
    }

    public MovieListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain the fragment through configuration changes.
        setRetainInstance(true);

        mSortKey = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        fetchMovies(mSortKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        GridView movieGrid = (GridView)rootView.findViewById(R.id.grid_movies);
        mAdapter = new MovieAdapter(getActivity(), R.layout.movielist_item, null);
        movieGrid.setAdapter(mAdapter);

        movieGrid.setClickable(true);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieSelectedListener(position);
            }
        });

        return rootView;
    }

    /**
     * Updates the movie listing in case the sort order has been changed.
     */
    private void updateSortOrder() {
        final String sortKey = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (sortKey != mSortKey)
        {
            mSortKey = sortKey;
            fetchMovies(sortKey);
        }
    }

    private void fetchMovies(String sortKey) {
        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED || mTask.cancel(true)) {
            mTask = new MovieFetchTask();
            mTask.setOnNewMoviesFetchedListener(new MovieFetchTask.OnNewMoviesFetchedListener() {
                @Override
                public void onNewMoviesFetched(Movie... newMovies) {
                    mAdapter.setNewMovies(newMovies);
                }
            });
            mTask.execute(sortKey, IMAGE_PATH);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /** Check the sort order when ever resuming */
        updateSortOrder();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (MovieListCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement MovieListCallbacks");
        }
    }

    private void onMovieSelectedListener(int position) {
        //Log.v("MovieListFragment", "onMovieSelectedListener (pos: " + position + "), movie:" + mAdapter.getItem(position));

        Movie movie = (Movie) mAdapter.getItem(position);
        mCallback.onMovieSelectedListener(movie);
    }

}
