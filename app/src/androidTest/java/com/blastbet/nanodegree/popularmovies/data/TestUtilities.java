package com.blastbet.nanodegree.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Created by ilkka on 18.6.2016.
 */
public class TestUtilities extends AndroidTestCase {
    private static final String LOG_TAG = TestUtilities.class.getSimpleName();

    static ContentValues createNormalMovie(int movieId) {
        ContentValues normalMovie = new ContentValues();
        normalMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        normalMovie.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "This is a normal movie");
        normalMovie.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-01-01");
        normalMovie.put(MovieContract.MovieEntry.COLUMN_RUNTIME, "100");
        normalMovie.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "path");
        normalMovie.put(MovieContract.MovieEntry.COLUMN_TITLE, "Normal movie");
        normalMovie.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "3.0");
        return normalMovie;
    }

    static ContentValues createNormalReview(int movieId) {
        ContentValues normalReview = new ContentValues();
        normalReview.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        normalReview.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Norma Normal");
        normalReview.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "This was very normal movie");
        return normalReview;
    }

    static ContentValues createNormalTrailer(int movieId) {
        ContentValues normalTrailer = new ContentValues();
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_KEY, "normal-key");
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_NAME, "Normal movie trailer");
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_SITE, "Youtube");
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_SIZE, 1080);
        normalTrailer.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Trailer");
        return normalTrailer;
    }

    static long insertContent(Context context, String dataDescription,
                              String tableName, ContentValues content) {
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(tableName, null, content);

        assertFalse("Error: Could not insert " + dataDescription + " into database!", rowId == -1);

        Log.i(LOG_TAG, "Inserting content \"" + content.toString() + " to table: " + tableName);
        // Do a simple query to table where the data was inserted.
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        assertTrue("Error: No entries were found from the table \"" + tableName + "\" after insert",
                cursor.moveToFirst());

        ContentValues queriedContent = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, queriedContent);

        Log.i(LOG_TAG, "Database query result: " + queriedContent.toString());

        db.close();

        return rowId;
    }

    static long insertNormalMovie(Context context, int movieId) {
        ContentValues content = createNormalMovie(movieId);
        return insertContent(context, "normal movie", MovieContract.MovieEntry.TABLE_NAME, content);
    }

    static long insertNormalReview(Context context, int movieId) {
        ContentValues content = createNormalReview(movieId);
        return insertContent(context, "normal review", MovieContract.ReviewEntry.TABLE_NAME, content);
    }

    static long insertNormalTrailer(Context context, int movieId) {
        ContentValues content = createNormalTrailer(movieId);
        return insertContent(context, "normal trailer", MovieContract.TrailerEntry.TABLE_NAME, content);
    }

    static void validateRecord(Cursor cursor, ContentValues expected) {
        Set<Map.Entry<String, Object>> expectedValues = expected.valueSet();
        for (Map.Entry<String, Object> entry : expectedValues) {
            String column = entry.getKey();
            int columnIndex = cursor.getColumnIndex(column);
            assertFalse("Error: Column \"" + column + "\" was not found!", columnIndex == -1);
            String expectedValue = entry.getValue().toString();
            String value = cursor.getString(columnIndex);
            assertEquals("Error: Column \"" + column + "\" was \"" + value + "\", \"" +
                    expectedValue + "\" was expected!",
                    expectedValue, value);
        }
    }
}
