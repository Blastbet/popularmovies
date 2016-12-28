package com.blastbet.nanodegree.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.blastbet.nanodegree.popularmovies.data.MovieContract;
import com.blastbet.nanodegree.popularmovies.sync.MovieSyncAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieDetailsFragment extends Fragment       {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    // the fragment initialization parameter
    private static final String ARG_MOVIE = "movie_details";

    public static final String DETAILSFRAGMENT_TAG = "DF_TAG";

    private Cursor mDetailsCursor = null;
    private Cursor mTrailerCursor = null;
    private Cursor mReviewCursor = null;

    private long mMovieId = -1;

    private TrailerCursorAdapter mTrailerAdapter = null;
    private ReviewCursorAdapter mReviewAdapter = null;

    private Unbinder mUnbinder = null;

    @BindView(R.id.text_movie_detail_title) TextView mTitleView;
    @BindView(R.id.text_movie_detail_description) TextView mOverviewView;
    @BindView(R.id.text_movie_detail_production_year) TextView mReleaseDateView;
    @BindView(R.id.text_movie_detail_run_length) TextView mRuntimeView;
    @BindView(R.id.text_movie_detail_rating) TextView mRatingView;
    @BindView(R.id.image_movie_detail_poster) ImageView mPosterView;
    @BindView(R.id.trailer_list_view) RecyclerView mTrailerListView;
    @BindView(R.id.review_list_view) RecyclerView mReviewListView;
    @BindView(R.id.button_mark_as_favorite) ToggleButton mFavoriteButton;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy");

    private MoviePosterLoader mMoviePosterLoader = null;

    private Loader<Cursor> mDetailsLoader;
    private Loader<Cursor> mTrailersLoader;
    private Loader<Cursor> mReviewsLoader;

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    static final int COL_MOVIE_MOVIE_ID     = 1;
    static final int COL_MOVIE_TITLE        = 2;
    static final int COL_MOVIE_POSTER_PATH  = 3;
    static final int COL_MOVIE_OVERVIEW     = 4;
    static final int COL_MOVIE_RUNTIME      = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_VOTE_AVERAGE = 7;
    static final int COL_MOVIE_FAVORITE     = 8;

    private static final String[] MOVIE_REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    static final int COL_REVIEW_AUTHOR  = 1;
    static final int COL_REVIEW_CONTENT = 2;

    private static final String[] MOVIE_TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE
    };

    static final int COL_TRAILER_KEY  = 1;
    static final int COL_TRAILER_NAME = 2;
    static final int COL_TRAILER_SITE = 3;

    public static final String EXTRA_MOVIE_ID_KEY = "extraMovieId";

    private static final int MOVIE_DETAILS_LOADER = 1;
    private static final int MOVIE_REVIEWS_LOADER = 2;
    private static final int MOVIE_TRAILERS_LOADER = 3;

    //TODO: Add cursor loader for detailed data

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

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

        if (getArguments() != null) {
            mMovieId = getArguments().getLong(EXTRA_MOVIE_ID_KEY);
            Log.v(LOG_TAG, "Created new movieDetailsFragment with movie id: " + mMovieId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mTrailerAdapter = new TrailerCursorAdapter(getContext(), null);
        mTrailerListView.setAdapter(mTrailerAdapter);
        mTrailerListView.setLayoutManager(
                new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        mReviewAdapter = new ReviewCursorAdapter(getContext(), null);
        mReviewListView.setAdapter(mReviewAdapter);
        mReviewListView.setLayoutManager(
                new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Bundle args = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_MOVIE_ID_KEY)) {
            args = savedInstanceState;
            mMovieId = savedInstanceState.getLong(EXTRA_MOVIE_ID_KEY);
            Log.v(LOG_TAG, "Movie id from savedInstanceState: " + mMovieId);
        } else {
            args = new Bundle();
            args.putLong(EXTRA_MOVIE_ID_KEY, mMovieId);
            Log.v(LOG_TAG, "Movie id from class member: " + mMovieId);
        }
        if (mMovieId > 0) {
            getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, args, detailsLoaderCallbacks);
            getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, args, reviewsLoaderCallbacks);
            getLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, args, trailersLoaderCallbacks);
            reloadMovieDetails();
        } else {
            Log.v(LOG_TAG, "Movie id was invalid when activity for movieDetailsFragment was created");
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(EXTRA_MOVIE_ID_KEY, getArguments().getLong(EXTRA_MOVIE_ID_KEY));

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public  Loader<Cursor> createTrailersLoader(long movieId) {
        Uri uri = MovieContract.TrailerEntry.buildTrailerWithMovieIdUri(movieId);
        return new CursorLoader(getActivity(), uri, MOVIE_TRAILER_COLUMNS, null, null, "_id ASC");
    }

    public  Loader<Cursor> createReviewsLoader(long movieId) {
        Uri uri = MovieContract.ReviewEntry.buildReviewWithMovieIdUri(movieId);
        return new CursorLoader(getActivity(), uri, MOVIE_REVIEW_COLUMNS, null, null, "_id ASC");
    }

    public void reloadMovieDetails() {
        updateMovie(mMovieId);
    }


    public void updateMovie(long movieId) {
        Log.e(LOG_TAG, "UPDATE MOVIE: " + movieId);
        MovieSyncAdapter.syncDetailsNow(getActivity(), movieId);
        Bundle args = new Bundle();
        args.putLong(EXTRA_MOVIE_ID_KEY, movieId);
        mMovieId = movieId;
        getLoaderManager().restartLoader(MOVIE_DETAILS_LOADER, args, detailsLoaderCallbacks);
        getLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER, args, reviewsLoaderCallbacks);
        getLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER, args, trailersLoaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<Cursor> detailsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    long movieId = args.getLong(EXTRA_MOVIE_ID_KEY);
                    Uri uri = MovieContract.MovieEntry.buildMovieWithIdUri(movieId);
                    return new CursorLoader(getActivity(), uri, MOVIE_DETAIL_COLUMNS, null, null, "_id ASC");
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    setDetails(data);
                    mDetailsCursor = data;
                    Log.e(LOG_TAG, "Details loaded");
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {}
            };

    private LoaderManager.LoaderCallbacks<Cursor> trailersLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    long movieId = args.getLong(EXTRA_MOVIE_ID_KEY);
                    Log.v(LOG_TAG, "Creating trailers loader for movie " + movieId);
                    mTrailersLoader = createTrailersLoader(movieId);
                    return mTrailersLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    setTrailers(data);
                    Log.e(LOG_TAG, "Trailers loaded");
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {}
            };

    private LoaderManager.LoaderCallbacks<Cursor> reviewsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    long movieId = args.getLong(EXTRA_MOVIE_ID_KEY);
                    Log.v(LOG_TAG, "Creating reviews loader for movie " + movieId);
                    mReviewsLoader = createReviewsLoader(movieId);
                    return mReviewsLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    setReviews(data);
                    Log.e(LOG_TAG, "Reviews loaded");
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {}
            };

    public void setDetails(Cursor details) {
        if (!details.moveToFirst()) {
            Log.v(LOG_TAG, "Empty details received");
            return;
        }
        mTitleView.setText(details.getString(COL_MOVIE_TITLE));
        mOverviewView.setText(details.getString(COL_MOVIE_OVERVIEW));
        Date releaseDate = new Date(details.getLong(COL_MOVIE_RELEASE_DATE));
        mReleaseDateView.setText(mDateFormat.format(releaseDate));
        String runTime = details.getString(COL_MOVIE_RUNTIME);
        if (runTime != null) {
            mRuntimeView.setText(details.getString(COL_MOVIE_RUNTIME) + "min");
        }
        mRatingView.setText(details.getString(COL_MOVIE_VOTE_AVERAGE) + " / 10");

        if (details.getInt(COL_MOVIE_FAVORITE) == 0) {
            mFavoriteButton.setChecked(false);
            Log.v(LOG_TAG, "Favorite");
        } else {
            mFavoriteButton.setChecked(true);
            Log.v(LOG_TAG, "Not Favorite");
        }

        mFavoriteButton.setOnCheckedChangeListener(mFavoriteButtonListener);

        mMoviePosterLoader = new MoviePosterLoader(getContext(),
                details.getString(COL_MOVIE_POSTER_PATH));
        mMoviePosterLoader.loadMoviePoster(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mPosterView.setBackgroundColor(Color.WHITE);
                mPosterView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(LOG_TAG, "Failed to load poster image for movie \"" + (mTitleView != null ? mTitleView.getText() : "") +"\"");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                mPosterView.setBackground(placeHolderDrawable);
            }
        });
    }

    public void setTrailers(Cursor trailers) {
        Log.v(LOG_TAG, "Adding " + trailers.getCount() + " trailers for movie.");
        mTrailerAdapter.changeCursor(trailers);
    }

    public void setReviews(Cursor reviews) {
        Log.v(LOG_TAG, "Adding " + reviews.getCount() + " reviews for movie.");
        mReviewAdapter.changeCursor(reviews);
    }

    private ToggleButton.OnCheckedChangeListener mFavoriteButtonListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, b ? true : false);
                    ContentResolver resolver = getContext().getContentResolver();
                    final String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
                    resolver.update(MovieContract.MovieEntry.CONTENT_URI, cv, selection,
                            new String[]{Long.toString(mMovieId)});
                    Log.v(LOG_TAG, "Setting movie " + mMovieId + " as favorite.");
                }
            };
}
