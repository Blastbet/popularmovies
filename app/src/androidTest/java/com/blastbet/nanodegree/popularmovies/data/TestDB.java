package com.blastbet.nanodegree.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by ilkka on 18.6.2016.
 */
public class TestDB extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNames.add(MovieContract.ReviewEntry.TABLE_NAME);
        tableNames.add(MovieContract.TrailerEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);

        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());
    }
}


