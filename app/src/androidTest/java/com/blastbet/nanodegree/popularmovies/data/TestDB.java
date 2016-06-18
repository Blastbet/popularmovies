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

        assertTrue("Error: The database has not been created correctly", c.moveToFirst());

        do {
            tableNames.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: The was created without all movie, review and trailer entry tables",
                tableNames.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for movie table information.",
                c.moveToFirst());

        final HashSet<String> columns = new HashSet<String>();
        columns.add(MovieContract.MovieEntry._ID);
        columns.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        columns.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        columns.add(MovieContract.MovieEntry.COLUMN_RELEASEDATE);
        columns.add(MovieContract.MovieEntry.COLUMN_RUNTIME);
        columns.add(MovieContract.MovieEntry.COLUMN_TITLE);
        columns.add(MovieContract.MovieEntry.COLUMN_VOTEAVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columns.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for review table information.",
                c.moveToFirst());

        columns.add(MovieContract.ReviewEntry._ID);
        columns.add(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
        columns.add(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        columns.add(MovieContract.ReviewEntry.COLUMN_CONTENT);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required review entry columns",
                columns.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for trailer table information.",
                c.moveToFirst());

        columns.add(MovieContract.TrailerEntry._ID);
        columns.add(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);
        columns.add(MovieContract.TrailerEntry.COLUMN_KEY);
        columns.add(MovieContract.TrailerEntry.COLUMN_NAME);
        columns.add(MovieContract.TrailerEntry.COLUMN_SITE);
        columns.add(MovieContract.TrailerEntry.COLUMN_SIZE);
        columns.add(MovieContract.TrailerEntry.COLUMN_TYPE);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required trailer entry columns",
                columns.isEmpty());

        db.close();
    }
}


