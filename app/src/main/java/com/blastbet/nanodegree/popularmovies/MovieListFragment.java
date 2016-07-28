package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.blastbet.nanodegree.popularmovies.tmdb.MovieList;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieApi;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;

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
    private Call<MovieList> mMovieListCall;
    private MovieApi mMovieApi;

    public interface MovieListCallback {
        void onMovieSelectedListener(Movie movie);
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

        mMovieApi = mRetrofit.create(MovieApi.class);
        fetchMovies(mSortKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        RecyclerView movieGrid = (RecyclerView)rootView.findViewById(R.id.grid_movies);
        movieGrid.setHasFixedSize(true);

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

    private Callback<MovieList> mMovieListResponseCallback = new Callback<MovieList>() {
        @Override
        public void onResponse(Call<MovieList> call, Response<MovieList> response) {
            MovieList movieList = response.body();
            mAdapter.setNewMovies(movieList.getMovieList());
        }

        @Override
        public void onFailure(Call<MovieList> call, Throwable t) {
            Log.e(LOG_TAG, "Failed to fetch movie list! " + t.getMessage());
            Toast.makeText(getContext(), "Failed to fetch movie list!", Toast.LENGTH_LONG).show();
        }
    };

    private void fetchMovies(String sortKey) {
        mMovieListCall = mMovieApi.getMovieList(sortKey, BuildConfig.THEMOVIEDB_API_KEY);
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

        Movie movie = (Movie) mAdapter.getItem(position);
        mCallback.onMovieSelectedListener(movie);
    }

}
