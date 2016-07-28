package com.blastbet.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.blastbet.nanodegree.popularmovies.tmdb.Movie;

public class MovieListActivity extends AppCompatActivity implements MovieListFragment.MovieListCallback {

    private boolean mTwoPane;
    private static final String DETAILSFRAGMENT_TAG = "DF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            if (savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment, DETAILSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
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

    /**
     * Handler for clicks on the movie posters. Brings up the detail view for the movie
     * @param movie The movie data to show.
     */
    public void onMovieSelectedListener(Movie movie) {

        MovieDetailsFragment detailsFragment =
                (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie_details);

        Log.v("MovieListActivity", "onMovieSelectedListener, movie:" + movie.getTitle());
        if (detailsFragment == null) {
            // Small display, phone e.g.
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            detailsIntent.putExtra(getString(R.string.movie_extra), movie);
            startActivity(detailsIntent);
        }
        else
        {
            Log.e("MovieListActivity", "Tablet layout for detailed view is not yet implemented!");
        }

    }
}
