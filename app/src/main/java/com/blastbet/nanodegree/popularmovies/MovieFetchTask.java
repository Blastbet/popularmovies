package com.blastbet.nanodegree.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ilkka on 6.4.2016.
 */
public class MovieFetchTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = MovieFetchTask.class.getSimpleName();
    private final String API_KEY_PARAM = "api_key";

    private OnNewMoviesFetchedListener mMoviesFetchedListener = null;

    public interface OnNewMoviesFetchedListener {
        void onNewMoviesFetched(Movie... newMovies);
    }

    public void setOnNewMoviesFetchedListener(OnNewMoviesFetchedListener listener) {
        mMoviesFetchedListener = listener;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        if (params == null || params.length != 1) {
            return null;
        }

        final String queryPath = params[0];

        Movie[] movies = fetchMovieList(queryPath);

        if (movies == null) {
            return null;
        }

        for (int i = 0; i < movies.length; i++)
        {
            String runTime = fetchMovieDetail(movies[i].getId(), "runtime");
            movies[i].setRuntime(runTime + "min");
        }

        return movies;
    }

    /** Fetches the list of movies using the sort (queryParam) key provided */
    private Movie[] fetchMovieList(String queryParam) {
        String movieJsonString = null;
        final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(queryParam)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                .build();

        movieJsonString = doHttpGet(builtUri);

        try {
            return parseMovieJson(movieJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Movie Json parsing failed! ", e);
        }
        return null;
    }

    /** Fetches a detail information for a specific movie, matching the movieId. */
    private String fetchMovieDetail(String movieId, String param) {
        String movieJsonString = null;
        final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                .build();

        movieJsonString = doHttpGet(builtUri);

        try {
            JSONObject root = new JSONObject((movieJsonString));
            return root.getString(param);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Movie details Json parsing failed! ", e);
        }
        return null;
    }

    /** Runs an HTTP GET operation on the URI provided and returns the received string */
    private String doHttpGet(Uri uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String receivedString = null;

        try {
            URL url = new URL(uri.toString());
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

            receivedString = buffer.toString();

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
        return receivedString;
    }

    /** Parses a movie listing JSON */
    Movie[] parseMovieJson(String movieJsonString) throws JSONException {

        if (movieJsonString == null) {
            return null;
        }

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
            String posterPath = movieJson.getString(MDB_POSTER);
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
            Movie movie = new Movie(posterPath, id, title, overView, null, releaseDate, vote, voteCount);
            movies[i] = movie;
        }
        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if (mMoviesFetchedListener != null) {
            //Log.v("FetchMovies","New movies fetched");
            mMoviesFetchedListener.onNewMoviesFetched(movies);
        }
    }
}