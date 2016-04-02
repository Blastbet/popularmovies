package com.blastbet.nanodegree.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    MovieAdapter mAdapter = null;

    private FetchTask mTask = null;

    /** Dummy data */
/*    private static final Movie movie1 = new Movie(Uri.EMPTY, "Movie 1", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie2 = new Movie(Uri.EMPTY, "Movie 2", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie3 = new Movie(Uri.EMPTY, "Movie 3", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie4 = new Movie(Uri.EMPTY, "Movie 4", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie5 = new Movie(Uri.EMPTY, "Movie 5", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie6 = new Movie(Uri.EMPTY, "Movie 6", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movie7 = new Movie(Uri.EMPTY, "Movie 7", "joo joo", new Date(20000000), "en", "1.0", 1);
    private static final Movie movies[] = {movie1, movie2, movie3, movie4, movie5, movie6, movie7};

    interface DataFetchCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    private DataFetchCallbacks mCallbacks;
    private FetchTask mTask;
*/
    public MovieListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain the fragment through configuration changes.
        setRetainInstance(true);

        mTask = new FetchTask();
        mTask.execute("popular", "http://image.tmdb.org/t/p/w185/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        GridView movieGrid = (GridView)rootView.findViewById(R.id.grid_movies);
        mAdapter = new MovieAdapter(getActivity(), R.layout.movielist_item, null);
        movieGrid.setAdapter(mAdapter);
        return rootView;
    }

    private class FetchTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params == null || params.length < 2) {
                return null;
            }

            final String queryPath = params[0];
            final String posterBasePath = params[1];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonString = null;

            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(queryPath)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieJsonString = buffer.toString();

                Log.v(LOG_TAG, "Queried for " + queryPath + " movies. Received Json: " + movieJsonString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream!");
                    }
                }
            }

            try {
                return parseMovieJson(movieJsonString, posterBasePath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        Movie[] parseMovieJson(String movieJsonString, String posterBasePath) throws JSONException {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            Movie movies[] = null;

            final String MDB_RESULTS = "results";
            final String MDB_POSTER = "poster_path";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE = "release_date";
            final String MDB_ID = "id";
            final String MDB_TITLE = "original_title";
            final String MDB_VOTE_AVG = "vote_average";
            final String MDB_VOTE_COUNT = "vote_count";

            JSONObject root = new JSONObject((movieJsonString));
            JSONArray results = root.getJSONArray(MDB_RESULTS);

            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++)
            {
                JSONObject movieJson = results.getJSONObject(i);
                String posterPath = posterBasePath + movieJson.getString(MDB_POSTER);
                String overView = movieJson.getString(MDB_OVERVIEW);
                Date releaseDate = null;
                try {
                    releaseDate = dateFormat.parse(movieJson.getString(MDB_RELEASE));
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Release date parsing failed!");
                }
                calendar.setTime(releaseDate);
                String id = movieJson.getString(MDB_ID);
                String title = movieJson.getString(MDB_TITLE);
                String voteCount = movieJson.getString(MDB_VOTE_COUNT);
                String vote = movieJson.getString(MDB_VOTE_AVG);
                Movie movie = new Movie(Uri.parse(posterPath), id, title, overView, releaseDate, vote, voteCount);
                movies[i] = movie;
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mAdapter.setNewMovies(movies);
        }
    }
}
