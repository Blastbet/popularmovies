package com.blastbet.nanodegree.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ilkka on 12.6.2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.blastbet.nanodegree.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_REVIEW = "review";
    public static final String PATH_UNREFERENCED_REVIEW = "unreferenced_review";

    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_UNREFERENCED_TRAILER = "unreferenced_trailer";

    public static final String PATH_POPULAR_MOVIE = "popular";
    public static final String PATH_TOP_RATED_MOVIE = "top_rated";

    public static final String PATH_OTHER_MOVIE = "other_movie";

    public static final Uri URI_OTHER_MOVIE =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_OTHER_MOVIE).build();

    public static final Uri URI_UNREFERENCED_TRAILER =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_UNREFERENCED_TRAILER).build();

    public static final Uri URI_UNREFERENCED_REVIEW =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_UNREFERENCED_REVIEW).build();


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        // TMDB id for the movie
        public static final String COLUMN_MOVIE_ID    = "movie_id";

        public static final String COLUMN_TITLE        = "title";
        public static final String COLUMN_POSTER_PATH  = "poster_path";
        public static final String COLUMN_OVERVIEW     = "overview";
        public static final String COLUMN_RUNTIME      = "runtime";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_FAVORITE     = "favorite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithIdUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static Long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class PopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIE;

        public static final String TABLE_NAME = "popular_movie";

        // TMDB id for the movie (foreign key into the movie table)
        public static final String COLUMN_MOVIE_ID    = "movie_id";

        public static final Uri buildPopularMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TopRatedEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIE;

        public static final String TABLE_NAME = "top_rated_movie";

        // TMDB id for the movie (foreign key into the movie table)
        public static final String COLUMN_MOVIE_ID    = "movie_id";

        public static final Uri buildTopRatedMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID    = "review_id";
        // TMDB id for the movie (foreign key into the movie table)
        public static final String COLUMN_MOVIE_ID    = "movie_id";

        public static final String COLUMN_AUTHOR      = "author";
        public static final String COLUMN_CONTENT     = "content";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewWithMovieIdUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static Long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        // TMDB id for the movie (foreign key into the movie table)
        public static final String COLUMN_MOVIE_ID    = "movie_id";

        public static final String COLUMN_KEY         = "key";
        public static final String COLUMN_NAME        = "name";
        public static final String COLUMN_SITE        = "site";
        public static final String COLUMN_SIZE        = "size";
        public static final String COLUMN_TYPE        = "type";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerWithMovieIdUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static Long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
