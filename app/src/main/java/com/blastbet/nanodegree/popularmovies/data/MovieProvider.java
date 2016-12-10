package com.blastbet.nanodegree.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ilkka on 19.6.2016.
 */
public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    MovieDBHelper mOpenHelper;

    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static final int POPULAR_MOVIE = 200;
    private static final int FAVORITE_MOVIE = 300;
    private static final int TOP_RATED_MOVIE = 400;

    private static final int OTHER_MOVIE = 900;

    private static final int REVIEW = 2000;
    private static final int TRAILER = 3000;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(AUTHORITY, MovieContract.PATH_MOVIE          , MOVIE);

        matcher.addURI(AUTHORITY, MovieContract.PATH_FAVORITE_MOVIE , FAVORITE_MOVIE);
        matcher.addURI(AUTHORITY, MovieContract.PATH_POPULAR_MOVIE  , POPULAR_MOVIE);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TOP_RATED_MOVIE, TOP_RATED_MOVIE);
        matcher.addURI(AUTHORITY, MovieContract.PATH_OTHER_MOVIE    , OTHER_MOVIE);

        matcher.addURI(AUTHORITY, MovieContract.PATH_REVIEW + "/#"   , REVIEW);
        matcher.addURI(AUTHORITY, MovieContract.PATH_TRAILER + "/#"  , TRAILER);
        matcher.addURI(AUTHORITY, MovieContract.PATH_MOVIE + "/#"   , MOVIE_WITH_ID);

        return matcher;
    }

    private static SQLiteQueryBuilder sFavoriteMoviesQueryBuilder;
    static {
        sFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMoviesQueryBuilder.setTables(
                MovieContract.FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private static String sOtherMoviesSelection =
            MovieContract.MovieEntry.COLUMN_FAVORITE + "=0" +
                    " AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                    " NOT IN (SELECT " + MovieContract.PopularEntry.COLUMN_MOVIE_ID +
                    " FROM " + MovieContract.PopularEntry.TABLE_NAME + ")" +
                    " AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                    " NOT IN (SELECT " + MovieContract.TopRatedEntry.COLUMN_MOVIE_ID +
                    " FROM " + MovieContract.TopRatedEntry.TABLE_NAME + ");";

    private static SQLiteQueryBuilder sOtherMoviesQueryBuilder;
    static {
        sOtherMoviesQueryBuilder = new SQLiteQueryBuilder();
        sOtherMoviesQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME +
                        " WHERE " +
                        sOtherMoviesSelection
        );
    }


    private static SQLiteQueryBuilder sPopularMoviesQueryBuilder;
    static {
        sPopularMoviesQueryBuilder = new SQLiteQueryBuilder();
        sPopularMoviesQueryBuilder.setTables(
                MovieContract.PopularEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.PopularEntry.TABLE_NAME +
                        "." + MovieContract.PopularEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private static SQLiteQueryBuilder sTopRatedMoviesQueryBuilder;
    static {
        sTopRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        sTopRatedMoviesQueryBuilder.setTables(
                MovieContract.TopRatedEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TopRatedEntry.TABLE_NAME +
                        "." + MovieContract.TopRatedEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private static SQLiteQueryBuilder sMovieWithReviewAndTrailerQueryBuilder;

    static {
        sMovieWithReviewAndTrailerQueryBuilder = new SQLiteQueryBuilder();
        sMovieWithReviewAndTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID
        );
    }

    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                    " = ? ";

    private static final String sReviewIdSelection =
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
                    " = ? ";

    private static final String sTrailerIdSelection =
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID +
                    " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        long movieId;
        Cursor retCursor = null;
        Log.v(LOG_TAG, "Query for Uri: " + uri);
        switch (match) {
            case MOVIE:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_MOVIE:
                retCursor = sFavoriteMoviesQueryBuilder.query(
                        db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case POPULAR_MOVIE:
                retCursor = sPopularMoviesQueryBuilder.query(
                        db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TOP_RATED_MOVIE:
                retCursor = sTopRatedMoviesQueryBuilder.query(
                        db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case OTHER_MOVIE:
                retCursor = sOtherMoviesQueryBuilder.query(
                        db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, sMovieIdSelection, new String[]{Long.toString(movieId)},
                        null, null, sortOrder);
                break;
            case REVIEW:
                movieId = MovieContract.ReviewEntry.getIdFromUri(uri);
                retCursor = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection, sReviewIdSelection, new String[]{Long.toString(movieId)},
                        null, null, sortOrder);
                break;
            case TRAILER:
                movieId = MovieContract.TrailerEntry.getIdFromUri(uri);
                retCursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection, sTrailerIdSelection, new String[]{Long.toString(movieId)},
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_MOVIE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case POPULAR_MOVIE:
                return MovieContract.PopularEntry.CONTENT_TYPE;
            case TOP_RATED_MOVIE:
                return MovieContract.TopRatedEntry.CONTENT_TYPE;
            case OTHER_MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case MOVIE: {
                // Movie details in the default case should be bulkInserted initially so there _should_
                // already be an entry here, thus using the conflict resolve rule CONFLICT_REPLACE
                final long _id = db.insertWithOnConflict(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case FAVORITE_MOVIE: {
                final long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case POPULAR_MOVIE: {
                final long _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.PopularEntry.buildPopularMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TOP_RATED_MOVIE: {
                final long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TopRatedEntry.buildTopRatedMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                final long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewWithMovieIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILER: {
                final long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerWithMovieIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (returnUri != null) {
            getContext().getContentResolver().notifyChange(returnUri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case OTHER_MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, sOtherMoviesSelection, null);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                final long movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME, values,
                        sMovieIdSelection, new String[]{Long.toString(movieId)});
                break;
            case FAVORITE_MOVIE:
                rowsUpdated = db.update(
                        MovieContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POPULAR_MOVIE:
                rowsUpdated = db.update(
                        MovieContract.PopularEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TOP_RATED_MOVIE:
                rowsUpdated = db.update(
                        MovieContract.TopRatedEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int insertCount = 0;
        switch(match) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        //Log.v(LOG_TAG, "Inserting to movie table: " + value.toString());
                        final long _id = db.insertWithOnConflict(
                                MovieContract.MovieEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case FAVORITE_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        final long _id = db.insertWithOnConflict(
                                MovieContract.FavoriteEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case POPULAR_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        //Log.v(LOG_TAG, "Inserting to popular movie table: " + value.toString());
                        final long _id = db.insertWithOnConflict(
                                MovieContract.PopularEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TOP_RATED_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        final long _id = db.insertWithOnConflict(
                                MovieContract.TopRatedEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        final long _id = db.insertWithOnConflict(
                                MovieContract.ReviewEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TRAILER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        final long _id = db.insertWithOnConflict(
                                MovieContract.TrailerEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        Log.v(LOG_TAG, "notifying for change in uri: " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return insertCount;
    }
}