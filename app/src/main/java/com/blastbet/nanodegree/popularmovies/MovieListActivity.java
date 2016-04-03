package com.blastbet.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MovieListActivity extends AppCompatActivity implements MovieListFragment.MovieListCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movielist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMovieSelectedListener(Movie movie) {

        MovieDetailsFragment detailsFragment =
                (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_details);

        Log.v("MovieListActivity", "onMovieSelectedListener, movie:" + movie.getName());
        if (detailsFragment == null) {
            // Small display, phone e.g.
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            detailsIntent.putExtra(getString(R.string.movie_extra), movie);
            startActivity(detailsIntent);
        }

    }
}
