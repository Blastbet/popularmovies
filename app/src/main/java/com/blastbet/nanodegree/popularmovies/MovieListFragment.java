package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    private MovieAdapter mAdapter = null;
    private FetchTask mTask = null;
    private final String API_KEY_PARAM = "api_key";

    private MovieListCallback mCallback;

    public interface MovieListCallback {
        void onMovieSelectedListener(Movie movie);
    }

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

        movieGrid.setClickable(true);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieSelectedListener(position);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (MovieListCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement MovieListCallbacks");
        }
    }

    private void onMovieSelectedListener(int position) {
        Log.v("MovieListFragment", "onMovieSelectedListener (pos: " + position + "), movie:" + mAdapter.getItem(position));

        Movie movie = (Movie) mAdapter.getItem(position);
        mCallback.onMovieSelectedListener(movie);
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

            Movie[] movies = fetchMovieList(queryPath, posterBasePath);

            if (movies == null) {
                return null;
            }

            for (int i = 0; i < movies.length; i++)
            {
                String runTime = fetchMovieDetail(movies[i].getId(), "runtime");
                movies[i].setRuntime(runTime);
            }

            return movies;
        }

        private Movie[] fetchMovieList(String queryParam, String posterBasePath) {
            String movieJsonString = null;
            final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(queryParam)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            movieJsonString = doHttpGet(builtUri);

            try {
                return parseMovieJson(movieJsonString, posterBasePath);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Movie Json parsing failed! ", e);
            }
            return null;
        }

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

        Movie[] parseMovieJson(String movieJsonString, String posterBasePath) throws JSONException {

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
                Uri posterUri = Uri.parse(posterPath).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                        .build();
                Movie movie = new Movie(posterUri, id, title, overView, null, releaseDate, vote, voteCount);
                movies[i] = movie;
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            Log.v("FetchMovies","New movies fetched");
            mAdapter.setNewMovies(movies);
        }
    }
}
