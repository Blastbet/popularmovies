package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
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
import android.widget.Toast;

import com.blastbet.nanodegree.tmdb.TMBDMovieListResponse;
import com.blastbet.nanodegree.tmdb.TMDBApi;
import com.blastbet.nanodegree.tmdb.TMDBMovie;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListFragment extends Fragment {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private MovieAdapter mAdapter = null;

    private String mSortKey = null;

    private MovieListCallback mCallback;

    private Retrofit mRetrofit;
    private Call<TMBDMovieListResponse> mMovieListCall;
    private TMDBApi mTMDBApi;

    public interface MovieListCallback {
        void onMovieSelectedListener(TMDBMovie movie);
    }

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

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MOVIEDB_BASE_URL)
                .build();

        mTMDBApi = mRetrofit.create(TMDBApi.class);
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

    private Callback<TMBDMovieListResponse> mMovieListResponseCallback = new Callback<TMBDMovieListResponse>() {
        @Override
        public void onResponse(Call<TMBDMovieListResponse> call, Response<TMBDMovieListResponse> response) {
            TMBDMovieListResponse movieListResponse = response.body();
            mAdapter.setNewMovies(movieListResponse.getMovieList());
        }

        @Override
        public void onFailure(Call<TMBDMovieListResponse> call, Throwable t) {
            Log.e(LOG_TAG, "Failed to fetch movie list! " + t.getMessage());
            Toast.makeText(getContext(), "Failed to fetch movie list!", Toast.LENGTH_LONG).show();
        }
    };

    private void fetchMovies(String sortKey) {
        mMovieListCall = mTMDBApi.getMovieList(sortKey, BuildConfig.THEMOVIEDB_API_KEY);
        mMovieListCall.enqueue(mMovieListResponseCallback);
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

        TMDBMovie movie = (TMDBMovie) mAdapter.getItem(position);
        mCallback.onMovieSelectedListener(movie);
    }

}
