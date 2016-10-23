package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.sync.MovieSyncAdapter;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;

public class MovieListFragment
        extends Fragment
        implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private MovieCursorAdapter mAdapter = null;
    private static final int MOVIE_LIST_LOADER = 0;

    private String mSortKey = null;

    private MovieListCallback mCallback;

    public interface MovieListCallback {
        void onMovieSelectedListener(long movieId);
    }

    private static final String[] MOVIE_COLUMNS = {
        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    //static final int COL_MOVIE_ROW_ID = 0;
    static final int COL_MOVIE_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";

        // Retain the fragment through configuration changes.
        setRetainInstance(true);

        mSortKey = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        PreferenceManager.getDefaultSharedPreferences(getActivity()).
                registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        RecyclerView movieGrid = (RecyclerView)rootView.findViewById(R.id.grid_movies);
        //movieGrid.setHasFixedSize(true);
        movieGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new MovieCursorAdapter(getContext(), null, R.layout.movielist_item, new MovieCursorAdapter.OnMovieClickedListener() {
            @Override
            public void onClick(long movieId) {
                mCallback.onMovieSelectedListener(movieId);
            }
        });
        movieGrid.setAdapter(mAdapter);

        return rootView;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Activity created, Creating the loader.");
        getLoaderManager().initLoader(MOVIE_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        String sortOrder;

        if (mSortKey.equalsIgnoreCase("popular")) {
            uri = MovieContract.PopularEntry.CONTENT_URI;
            sortOrder = MovieContract.PopularEntry.TABLE_NAME +
                    "." + MovieContract.PopularEntry._ID + " ASC";
        } else if (mSortKey.equalsIgnoreCase("top_rated")){
            uri = MovieContract.TopRatedEntry.CONTENT_URI;
            sortOrder = MovieContract.TopRatedEntry.TABLE_NAME +
                    "." + MovieContract.TopRatedEntry._ID + " ASC";
        } else if (mSortKey.equalsIgnoreCase("favorites")){
            uri = MovieContract.FavoriteEntry.CONTENT_URI;
            sortOrder = MovieContract.FavoriteEntry.TABLE_NAME +
                    "." + MovieContract.FavoriteEntry._ID + " ASC";
        } else {
            throw new UnsupportedOperationException("Unsupported movie list selection (" + mSortKey + ")");
        }

        Log.v(LOG_TAG, "Creating cursor loader. for " + mSortKey + " list - uri:" + uri);
        return new CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "Cursor loader (" + loader.toString() + ")loading finished..");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Cursor loader (" + loader.toString() + ") reset..");
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String prefSortKey = getString(R.string.pref_sort_by_key);
        if (key == prefSortKey) {
            mSortKey = sharedPreferences.getString(prefSortKey, mSortKey);
            Log.v(LOG_TAG, "Sort preference changed to " + mSortKey + ", reloading movies");
            reloadMovies();
        }
    }

    public void updateMovies() {
        MovieSyncAdapter.syncListNow(getActivity());
        getLoaderManager().restartLoader(MOVIE_LIST_LOADER, null, this);
    }

    public void reloadMovies() {
        getLoaderManager().destroyLoader(MOVIE_LIST_LOADER);
        getLoaderManager().initLoader(MOVIE_LIST_LOADER, null, this);
    }


}
