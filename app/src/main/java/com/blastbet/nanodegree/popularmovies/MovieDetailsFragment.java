package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieApi;
import com.blastbet.nanodegree.popularmovies.tmdb.Movie;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieReview;
import com.blastbet.nanodegree.popularmovies.tmdb.MovieTrailer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    // the fragment initialization parameter
    private static final String ARG_MOVIE = "movie_details";

    private Movie mMovie;

    private List<MovieReview> mReviews;
    private List<MovieTrailer> mTrailers;

    private Unbinder mUnbinder = null;

    @BindView(R.id.text_movie_detail_title) TextView mTitleView;
    @BindView(R.id.text_movie_detail_description) TextView mOverviewView;
    @BindView(R.id.text_movie_detail_production_year) TextView mReleaseDateView;
    @BindView(R.id.text_movie_detail_run_length) TextView mRuntimeView;
    @BindView(R.id.text_movie_detail_rating) TextView mRatingView;
    @BindView(R.id.image_movie_detail_poster) ImageView mPosterView;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy");

    private MoviePosterLoader mMoviePosterLoader = null;

    private Loader<Cursor> mDetailsLoader;
    private Loader<Cursor> mTrailersLoader;
    private Loader<Cursor> mReviewsLoader;

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    private static final String[] MOVIE_REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    private static final String[] MOVIE_TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE,
            MovieContract.TrailerEntry.COLUMN_SIZE,
            MovieContract.TrailerEntry.COLUMN_TYPE
    };

    public static final String EXTRA_MOVIE_ID_KEY = "extraMovieId";

    private static final int MOVIE_DETAILS_LOADER = 1;
    private static final int MOVIE_REVIEWS_LOADER = 2;
    private static final int MOVIE_TRAILERS_LOADER = 3;

    //TODO: Add cursor loader for detailed data

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Id of the movie whose details to show.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static MovieDetailsFragment newInstance(long movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_MOVIE_ID_KEY, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";

        if (getArguments() != null) {
            long movieId = getArguments().getLong(EXTRA_MOVIE_ID_KEY);
//            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
/*        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MOVIEDB_BASE_URL)
                .build();

        mMovieApi = mRetrofit.create(MovieApi.class);*/
    }

/*    private void fetchRunTime() {
        mMovieCall = mMovieApi.getMovieDetails(mMovie.getId(), BuildConfig.THEMOVIEDB_API_KEY);
        mMovieCall.enqueue(mMovieCallback);
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Movie movie = null;

        Bundle args = getArguments();
        if (args != null) {
            movie = args.getParcelable(getString(R.string.movie_extra));
        } else {
            movie = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_extra));
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);


        if (movie == null)
        {
            /** Set dummy data if we for some reason did not have the movie details here */
            mTitleView.setText("---");
            mOverviewView.setText("---");
            mReleaseDateView.setText("---");
            mRuntimeView.setText("---");
            mRatingView.setText("---");
            mPosterView.setBackgroundColor(Color.GRAY);
        } else {
            mMovie = movie;

            /** Populate the view with the movie details */
            mTitleView.setText(movie.getTitle());
            mOverviewView.setText(movie.getOverview());
            final String releaseDate = mDateFormat.format(movie.getReleaseDate()).toString();
            mReleaseDateView.setText(releaseDate);
            if (movie.getRuntime() == null || movie.getRuntime().isEmpty()) {
                mRatingView.setText("-");
//                fetchRunTime();
            } else {
                mRuntimeView.setText(movie.getRuntime() + " min");
            }
            mRatingView.setText(movie.getVoteAverage());
            /** Use picasso to download the movie poster and also to fit the image into the
             * imageview provided
             */
            Rect rect = new Rect();
            mPosterView.getDrawingRect(rect);

            mMoviePosterLoader = new MoviePosterLoader(getContext(), mMovie);
            mMoviePosterLoader.loadMoviePoster(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mPosterView.setBackgroundColor(Color.WHITE);
                    mPosterView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e(LOG_TAG, "Failed to load poster image for movie \"" + mMovie.getTitle() +"\"");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    mPosterView.setBackground(placeHolderDrawable);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Callback<Movie> mMovieCallback = new Callback<Movie>() {
        @Override
        public void onResponse(Call<Movie> call, Response<Movie> response) {
            final String runtime = response.body().getRuntime();
            mMovie.setRuntime(runtime);
            mRuntimeView.setText(runtime + " min");
        }

        @Override
        public void onFailure(Call<Movie> call, Throwable t) {
            Toast.makeText(getContext(), "Failed to fetch movie runtime!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, getArguments(), this);
        getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, getArguments(), this);
        getLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, getArguments(), this);
        super.onActivityCreated(savedInstanceState);
    }

    public  Loader<Cursor> createDetailsLoader(long movieId) {
        Uri uri = MovieContract.MovieEntry.buildMovieWithIdUri(movieId);
        return new CursorLoader(getActivity(), uri, MOVIE_DETAIL_COLUMNS, null, null, "_id ASC");
    }

    public  Loader<Cursor> createTrailersLoader(long movieId) {
        Uri uri = MovieContract.TrailerEntry.buildTrailerWithMovieIdUri(movieId);
        return new CursorLoader(getActivity(), uri, MOVIE_TRAILER_COLUMNS, null, null, "_id ASC");
    }

    public  Loader<Cursor> createReviewsLoader(long movieId) {
        Uri uri = MovieContract.ReviewEntry.buildReviewWithMovieIdUri(movieId);
        return new CursorLoader(getActivity(), uri, MOVIE_REVIEW_COLUMNS, null, null, "_id ASC");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long movieId = args.getLong(EXTRA_MOVIE_ID_KEY);

        switch (id) {
            case MOVIE_DETAILS_LOADER:
                mDetailsLoader = createDetailsLoader(movieId);
                return mDetailsLoader;
            case MOVIE_TRAILERS_LOADER:
                mTrailersLoader = createTrailersLoader(movieId);
                return mTrailersLoader;
            case MOVIE_REVIEWS_LOADER:
                mReviewsLoader = createReviewsLoader(movieId);
                return mReviewsLoader;
            default:
                throw new UnsupportedOperationException("Invalid loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader == mDetailsLoader) {
            Log.e(LOG_TAG, "Details loaded");
        } else if (loader == mTrailersLoader) {
            Log.e(LOG_TAG, "Trailers loaded");
        } else if (loader == mReviewsLoader) {
            Log.e(LOG_TAG, "Reviews loaded");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
