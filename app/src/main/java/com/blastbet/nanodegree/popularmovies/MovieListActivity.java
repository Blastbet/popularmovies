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

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            Log.v(LOG_TAG, "Two pane mode.");
            if (savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment(), MovieDetailsFragment.DETAILSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Log.v(LOG_TAG, "Single pane mode.");
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MovieListFragment mlf = (MovieListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movie_list);
        if (mlf != null) {
            mlf.updateMovies();
        }

        MovieDetailsFragment mdf = (MovieDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(MovieDetailsFragment.DETAILSFRAGMENT_TAG);
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
     * @param movieId The id of the movie for which to show details.
     */
    public void onMovieSelectedListener(long movieId) {
        Log.v("MovieListActivity", "onMovieSelectedListener, movie with id:" + movieId);

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putLong(MovieDetailsFragment.EXTRA_MOVIE_ID_KEY, movieId);
            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, detailsFragment, MovieDetailsFragment.DETAILSFRAGMENT_TAG)
                    .commit();
        } else {
            // Small display, phone e.g.
            Intent detailsIntent = new Intent(this, MovieDetailsActivity.class);
            detailsIntent.putExtra(MovieDetailsFragment.EXTRA_MOVIE_ID_KEY, movieId);
            startActivity(detailsIntent);
        }
    }
}
