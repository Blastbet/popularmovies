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
import android.database.Cursor;
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
import com.blastbet.nanodegree.popularmovies.tmdb.MovieReview;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieReviewList;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieTrailer;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieTrailerList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ilkka on 19.8.2016.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String MOVIE_SYNC_EXTRAS_MOVIE_ID = "movie_sync_id";

    private static final int MOVIE_SYNC_TYPE_GET_DETAILS = 1;
    private static final int MOVIE_SYNC_TYPE_LIST        = 2;

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

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MOVIEDB_BASE_URL)
                .client(httpClient.build())
                .build();

        mMovieApi = mRetrofit.create(MovieApi.class);
    }

    private String getSortKey() {
        return PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(
                        getContext().getString(R.string.pref_sort_by_key),
                        getContext().getString(R.string.pref_sort_by_default)
                );
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        int syncType;
        long movieId = 0L;

        if (bundle.containsKey(MOVIE_SYNC_EXTRAS_MOVIE_ID)) {
            syncType = MOVIE_SYNC_TYPE_GET_DETAILS;
            movieId = bundle.getLong(MOVIE_SYNC_EXTRAS_MOVIE_ID);
            Log.v(LOG_TAG, "Synchronizing details for movie: " + movieId);
        } else {
            syncType = MOVIE_SYNC_TYPE_LIST;
        }

        StringBuilder logEntryBuilder = new StringBuilder("Performing sync with extra bundle:\n");
        for (String key : bundle.keySet()) {
            logEntryBuilder.append('\t');
            logEntryBuilder.append(key);
            logEntryBuilder.append(": ");
            logEntryBuilder.append(bundle.get(key).toString());
            logEntryBuilder.append('\n');
        }
        Log.v(LOG_TAG, logEntryBuilder.toString());

        switch (syncType) {
            case MOVIE_SYNC_TYPE_LIST:
                final String sortKey = getSortKey();
                if (sortKey.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_favorites))) {
                    Log.v(LOG_TAG, "Not updating favorites list");
                    return;
                }
                final MovieList movies = fetchMovies(sortKey);
                updateMoviesList(movies, sortKey);
                cleanUnreferencedMovies();
                cleanUnreferencedReviews();
                cleanUnreferencedTrailers();
                break;
            case MOVIE_SYNC_TYPE_GET_DETAILS:
                final Movie movie = fetchMovieDetails(movieId);
                if (movie != null) {
                    updateMovieDetails(movie);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown movie sync type: " + syncType);
        }
    }

    private Movie fetchMovieDetails(long movieId) {
        Log.e(LOG_TAG, "Fetching movie details for movie #" + movieId);
        Call<Movie> movieCall = mMovieApi.getMovieDetails(
                movieId, BuildConfig.THEMOVIEDB_API_KEY
        );
        Call<MovieReviewList> reviewCall = mMovieApi.getMovieReviews(
                movieId, BuildConfig.THEMOVIEDB_API_KEY
        );
        Call<MovieTrailerList> trailerCall = mMovieApi.getMovieTrailers(
                movieId, BuildConfig.THEMOVIEDB_API_KEY
        );

        Movie movie = null;
        try {
            movie = movieCall.execute().body();
            final List<MovieReview> movieReviews = reviewCall.execute().body().getMovieReviews();
            Log.e(LOG_TAG, "Received " + movieReviews.size() + " reviews");
            movie.setReviews(movieReviews);
            final List<MovieTrailer> movieTrailers = trailerCall.execute().body().getMovieTrailers();
            Log.e(LOG_TAG, "Received " + movieTrailers.size() + " trailers");
            movie.setTrailers(movieTrailers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movie;
    }

    private MovieList fetchMovies(String sortKey) {
        mMovieListCall = mMovieApi.getMovieList(sortKey, BuildConfig.THEMOVIEDB_API_KEY);
        MovieList movieList = null;
        try {
            movieList = mMovieListCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Movie> movies = movieList.getMovieList();

        return movieList;
    }


    private ContentValues movieToDetailedContentValues(final Movie movie) {
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

    private ContentValues movieToPlainIdContentValues(final Movie movie) {
        final ContentValues cValues = new ContentValues();
        cValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        return cValues;
    }

    private void addTrailersToContentValuesVector(long movieId, List<MovieTrailer> trailers, Vector<ContentValues> cVector) {
        for (MovieTrailer trailer : trailers) {
            final ContentValues cValues = new ContentValues();
            cValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            cValues.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.getKey());
            cValues.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());
            cValues.put(MovieContract.TrailerEntry.COLUMN_SITE, trailer.getSite());
            cValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, trailer.getSize());
            cValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, trailer.getType());
            cVector.add(cValues);
        }
    }

    private void addReviewsToContentValuesVector(long movieId, List<MovieReview> reviews, Vector<ContentValues> cVector) {
        for (MovieReview review : reviews) {
            final ContentValues cValues = new ContentValues();
            cValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            cValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            cValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            cVector.add(cValues);
        }
    }

    private void updateMovieDetails(final Movie movie) {
        // Insert movie first
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues movieValues = movieToDetailedContentValues(movie);
        resolver.update(MovieContract.MovieEntry.buildMovieWithIdUri(movie.getId()), movieValues,
                null, null);

        // ...then trailers
        Vector<ContentValues> trailerVector = new Vector<>(movie.getTrailers().size());
        addTrailersToContentValuesVector(movie.getId(), movie.getTrailers(), trailerVector);
        ContentValues trailersArray[] = new ContentValues[trailerVector.size()];
        trailerVector.toArray(trailersArray);
        int inserted = resolver.bulkInsert(MovieContract.TrailerEntry.buildTrailerWithMovieIdUri(movie.getId()), trailersArray);
        Log.d(LOG_TAG, "Trailers sync finished, inserted " + inserted + " rows to trailers table.");

        // ...and finally reviews
        Vector<ContentValues> reviewVector = new Vector<>(movie.getReviews().size());
        addReviewsToContentValuesVector(movie.getId(), movie.getReviews(), reviewVector);
        ContentValues reviewsArray[] = new ContentValues[reviewVector.size()];
        reviewVector.toArray(reviewsArray);
        inserted = resolver.bulkInsert(MovieContract.ReviewEntry.buildReviewWithMovieIdUri(movie.getId()), reviewsArray);
        Log.d(LOG_TAG, "Reviews sync finished, inserted " + inserted + " rows to reviews table.");
    }


    private Set<Long> getMovieIdsInDatabase(final List<Movie> movies) {
        // Construct string representation of an array of the movie IDs for querying IDs already
        // in the database
        String selectionArgs[] = new String[movies.size()];
        StringBuilder selection = new StringBuilder();
        ContentResolver resolver = getContext().getContentResolver();

        selection.append(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        selection.append(" IN (");
        for(int i = 0; i < movies.size(); i++) {
            if (i > 0) selection.append(',');

            selection.append('?');
            selectionArgs[i] = Long.toString(movies.get(i).getId());
        }
        selection.append(')');

        Cursor refListCursor = resolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                selection.toString(),
                selectionArgs,
                null);

        refListCursor.moveToFirst();
        final int count = refListCursor.getCount();

        Set<Long> result = new HashSet<>(count);

        for(int i = 0; i < count; i++) {
            result.add(refListCursor.getLong(0));
            refListCursor.moveToNext();
        }

        refListCursor.close();
        return result;
    }

    private void cleanUnreferencedMovies() {
        ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(MovieContract.URI_OTHER_MOVIE, null, null);
    }

    private void cleanUnreferencedReviews() {
        ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(MovieContract.URI_UNREFERENCED_REVIEW, null, null);
    }

    private void cleanUnreferencedTrailers() {
        ContentResolver resolver = getContext().getContentResolver();
        resolver.delete(MovieContract.URI_UNREFERENCED_TRAILER, null, null);
    }

    private void updateMoviesList(final MovieList movies, final String sortKey) {
        List<Movie> movieList = movies.getMovieList();
        Vector<ContentValues> fullDetailVector = null;
        Vector<ContentValues> movieIdVector = null;
        Set<Long> moviesToUpdate = getMovieIdsInDatabase(movieList);

        int numNewMovies = movieList.size() - moviesToUpdate.size();

        if (numNewMovies > 0) {
            fullDetailVector = new Vector<ContentValues>(numNewMovies);
            movieIdVector = new Vector<ContentValues>(numNewMovies);
        }

        ContentResolver resolver = getContext().getContentResolver();

        for(Movie movie: movieList) {
            if (moviesToUpdate.contains(movie.getId())) {
                Log.v(LOG_TAG, "Updating details for movie: " + movie.getTitle());
                ContentValues values = movieToDetailedContentValues(movie);
                resolver.update(MovieContract.MovieEntry.buildMovieWithIdUri(movie.getId()),
                        values, null, null);
            } else {
                Log.v(LOG_TAG, "New movie: " + movie.getId() + " - " + movie.getTitle());
                fullDetailVector.add(movieToDetailedContentValues(movie));
                movieIdVector.add(movieToPlainIdContentValues(movie));
            }
        }

        int moviesInserted = 0;

        if (fullDetailVector != null && fullDetailVector.size() > 0) {
            ContentValues cValueArray[] = new ContentValues[fullDetailVector.size()];
            fullDetailVector.toArray(cValueArray);
            moviesInserted = resolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cValueArray);

            Log.d(LOG_TAG, "Movie sync finished, inserted " + moviesInserted + " new movies.");

            ContentValues idValuesArray[] = new ContentValues[movieIdVector.size()];
            movieIdVector.toArray(idValuesArray);

            if (sortKey.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_popular))) {
//                int deleted = resolver.delete(MovieContract.PopularEntry.CONTENT_URI, "1", null);
//                Log.d(LOG_TAG, "Removed " + deleted + " rows from popular movies table");
                int inserted = resolver.bulkInsert(MovieContract.PopularEntry.CONTENT_URI, idValuesArray);
                Log.d(LOG_TAG, "Sync finished, inserted " + inserted + " rows to popular movies table.");
            } else if (sortKey.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_top_rated))) {
//                int deleted = resolver.delete(MovieContract.TopRatedEntry.CONTENT_URI, "1", null);
//                Log.d(LOG_TAG, "Removed " + deleted + " rows from top rated movies table");
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

    public static void syncListNow(Context context) {
        syncNow(getSyncAccount(context), null, context.getString(R.string.content_authority));
        Log.v(LOG_TAG, "Synchronizing movies");
    }

    public static void syncDetailsNow(Context context, long movieId) {
        Bundle extras = new Bundle();
        extras.putLong(MOVIE_SYNC_EXTRAS_MOVIE_ID, movieId);
        Log.v(LOG_TAG, "Synchronizing movies with extras: " + extras.toString());
        syncNow(getSyncAccount(context), extras, context.getString(R.string.content_authority));
    }

    private static void syncNow(final Account account, Bundle extras, final String authority) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Log.v(LOG_TAG, "Synchronizing movies with extras: " + extras.toString());
        ContentResolver.requestSync(account, authority, extras);
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


        // TODO: Enable periodic sync ?
        // ContentResolver.setSyncAutomatically(newAccount, authority, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncNow(newAccount, null, authority);
    }

    public static void init(Context context) {
        getSyncAccount(context);
    }
}
