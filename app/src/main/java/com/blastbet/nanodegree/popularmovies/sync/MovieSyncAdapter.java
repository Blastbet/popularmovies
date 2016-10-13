package com.blastbet.nanodegree.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.blastbet.nanodegree.popularmovies.BuildConfig;
import com.blastbet.nanodegree.popularmovies.R;
import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieApi;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieList;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ilkka on 19.8.2016.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    private static final int SYNC_INTERVAL = 60 * 60 * 24;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 24;
    private Retrofit mRetrofit;
    private Call<MovieList> mMovieListCall;
    private MovieApi mMovieApi;

    private static final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        final String sortKey = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default));

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MOVIEDB_BASE_URL)
                .build();

        mMovieApi = mRetrofit.create(MovieApi.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        final String sortKey = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getString(R.string.pref_sort_by_key),
                getContext().getString(R.string.pref_sort_by_default));


        final MovieList movies = fetchMovies(sortKey);
        updateMoviesList(movies, sortKey);
    }


    ContentValues movieToDetailedContentValues(final Movie movie) {
        final ContentValues cValues = new ContentValues();
        cValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        cValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        cValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
        cValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        return cValues;
    }

    ContentValues movieToPlainIdContentValues(final Movie movie) {
        final ContentValues cValues = new ContentValues();
        cValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        return cValues;
    }


    private MovieList fetchMovies(String sortKey) {
        mMovieListCall = mMovieApi.getMovieList(sortKey, BuildConfig.THEMOVIEDB_API_KEY);
        MovieList movieList = null;
        try {
            movieList = mMovieListCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieList;
    }

    private void updateMoviesList(final MovieList movies, final String sortKey) {
        List<Movie> movieList = movies.getMovieList();
        Vector<ContentValues> fullDetailVector = new Vector<ContentValues>(movieList.size());
        Vector<ContentValues> movieIdVector = new Vector<ContentValues>(movieList.size());

        for(Movie movie: movieList) {
            fullDetailVector.add(movieToDetailedContentValues(movie));
            movieIdVector.add(movieToPlainIdContentValues(movie));
        }

        ContentResolver resolver = getContext().getContentResolver();

        int moviesInserted = 0;

        if (fullDetailVector.size() > 0) {
            ContentValues cValueArray[] = new ContentValues[fullDetailVector.size()];
            fullDetailVector.toArray(cValueArray);
            moviesInserted = resolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cValueArray);

            Log.d(LOG_TAG, "Movie sync finished, inserted " + moviesInserted + " new movies.");

            ContentValues idValuesArray[] = new ContentValues[movieIdVector.size()];
            movieIdVector.toArray(idValuesArray);

            if (sortKey.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_popular))) {
                int deleted = resolver.delete(MovieContract.PopularEntry.CONTENT_URI, "1", null);
                Log.d(LOG_TAG, "Removed " + deleted + " rows from popular movies table");
                int inserted = resolver.bulkInsert(MovieContract.PopularEntry.CONTENT_URI, idValuesArray);
                Log.d(LOG_TAG, "Sync finished, inserted " + inserted + " rows to popular movies table.");
            } else if (sortKey.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_top_rated))) {
                int deleted = resolver.delete(MovieContract.TopRatedEntry.CONTENT_URI, "1", null);
                Log.d(LOG_TAG, "Removed " + deleted + " rows from top rated movies table");
                int inserted = resolver.bulkInsert(MovieContract.TopRatedEntry.CONTENT_URI, idValuesArray);
                Log.d(LOG_TAG, "Sync finished, inserted " + inserted + " rows to top rated movies table.");
            }

            // TODO: Remove movie rows that are do not match any entries in popular/toprated/favorite categories.
        }
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // Check if account exists
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void syncNow(Context context) {
        syncNow(getSyncAccount(context), context.getString(R.string.content_authority));
    }

    private static void syncNow(final Account account, final String authority) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, authority, bundle);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        // Configure periodic sync
        final String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(newAccount, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(newAccount, authority, new Bundle(), SYNC_INTERVAL);
        }


        // Enable periodic sync
        ContentResolver.setSyncAutomatically(newAccount, authority, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncNow(newAccount, authority);
    }

    public static void init(Context context) {
        getSyncAccount(context);
    }
}
