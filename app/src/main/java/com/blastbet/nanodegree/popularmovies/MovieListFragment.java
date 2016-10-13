package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.sync.MovieSyncAdapter;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private MovieCursorAdapter mAdapter = null;
    private static final int MOVIE_LIST_LOADER = 0;

    private String mSortKey = null;

    private MovieListCallback mCallback;

    public interface MovieListCallback {
        void onMovieSelectedListener(Movie movie);
    }

    private static final String[] MOVIE_COLUMNS = {
        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
        MovieContract.MovieEntry.COLUMN_MOVIE_ID,
        MovieContract.MovieEntry.COLUMN_POSTER_PATH
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
            public void onClick(int movieId) {
                mCallback.onMovieSelectedListener(movie);
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

    /**
     * Updates the movie listing in case the sort order has been changed.
     */
    private void updateSortOrder() {
        final String sortKey = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (sortKey != mSortKey) {
            mSortKey = sortKey;
            updateMovies();
        }
    }

    public void updateMovies() {
        MovieSyncAdapter.syncNow(getActivity());
        getLoaderManager().restartLoader(MOVIE_LIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        if (mSortKey.equalsIgnoreCase("popular")) {
            uri = MovieContract.PopularEntry.CONTENT_URI;
        } else if (mSortKey.equalsIgnoreCase("top_rated")){
            uri = MovieContract.TopRatedEntry.CONTENT_URI;
        } else if (mSortKey.equalsIgnoreCase("favorites")){
            uri = MovieContract.FavoriteEntry.CONTENT_URI;
        } else {
            throw new UnsupportedOperationException("Unsupported movie list selection (" + mSortKey + ")");
        }

        return new CursorLoader(getActivity(), uri, MOVIE_COLUMNS, null, null, "_id ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
