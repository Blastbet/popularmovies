package com.blastbet.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blastbet.nanodegree.popularmovies.tmdb.Movie;

import java.security.InvalidParameterException;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        final long movieId = getIntent().getLongExtra(MovieDetailsFragment.EXTRA_MOVIE_ID_KEY, -1);
        MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(movieId);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moviedetails, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        final long movieId = getIntent().getLongExtra(MovieDetailsFragment.EXTRA_MOVIE_ID_KEY, -1);

        MovieDetailsFragment mdf = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(MovieDetailsFragment.DETAILSFRAGMENT_TAG);
        if (mdf != null) {
            mdf.updateMovie(movieId);
        }
    }

}
