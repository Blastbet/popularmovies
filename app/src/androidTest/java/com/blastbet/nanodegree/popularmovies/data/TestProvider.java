package com.blastbet.nanodegree.popularmovies.data;

import android.database.Cursor;
import android.test.AndroidTestCase;
import com.blastbet.nanodegree.popularmovies.data.MovieContract.TrailerEntry;
import com.blastbet.nanodegree.popularmovies.data.MovieContract.MovieEntry;
import com.blastbet.nanodegree.popularmovies.data.MovieContract.ReviewEntry;
/**
 * Created by ilkka on 6.7.2016.
 */
public class TestProvider extends AndroidTestCase {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(TrailerEntry.CONTENT_URI, null, null);
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }


}
