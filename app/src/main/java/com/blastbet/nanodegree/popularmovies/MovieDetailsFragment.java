package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blastbet.nanodegree.tmdb.TMDBApi;
import com.blastbet.nanodegree.tmdb.TMDBMovie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    // the fragment initialization parameter
    private static final String ARG_MOVIE = "movie_details";

    private TMDBMovie mMovie;

    private TextView mTitleView = null;
    private TextView mReleaseDateView = null;
    private TextView mOverviewView = null;
    private TextView mRuntimeView = null;
    private TextView mRatingView = null;
    private ImageView mPosterView = null;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy");

    private MoviePosterLoader mMoviePosterLoader = null;

    private Retrofit mRetrofit;
    private Call<TMDBMovie> mMovieCall;
    private TMDBApi mTMDBApi;;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie Movie details to show.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static MovieDetailsFragment newInstance(TMDBMovie movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";

        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MOVIEDB_BASE_URL)
                .build();

        mTMDBApi = mRetrofit.create(TMDBApi.class);

    }

    private void fetchRunTime() {
        mMovieCall = mTMDBApi.getMovieDetails(mMovie.getId(), BuildConfig.THEMOVIEDB_API_KEY);
        mMovieCall.enqueue(mMovieCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        final TMDBMovie movie = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_extra));
        if (movie != null) {
            mMovie = movie;
        }

        mTitleView = (TextView) rootView.findViewById(R.id.text_movie_detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.text_movie_detail_description);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.text_movie_detail_production_year);
        mRuntimeView = (TextView) rootView.findViewById(R.id.text_movie_detail_run_length);
        mRatingView = (TextView) rootView.findViewById(R.id.text_movie_detail_rating);
        mPosterView = (ImageView) rootView.findViewById(R.id.image_movie_detail_poster);

        if (mMovie == null)
        {
            /** Set dummy data if we for some reason did not have the movie details here */
            mTitleView.setText("---");
            mOverviewView.setText("---");
            mReleaseDateView.setText("---");
            mRuntimeView.setText("---");
            mRatingView.setText("---");
            mPosterView.setBackgroundColor(Color.GRAY);
        } else {
            /** Populate the view with the movie details */
            mTitleView.setText(movie.getTitle());
            mOverviewView.setText(movie.getOverview());
            final String releaseDate = mDateFormat.format(movie.getReleaseDate()).toString();
            mReleaseDateView.setText(releaseDate);
            if (movie.getRuntime() == null || movie.getRuntime().isEmpty()) {
                mRatingView.setText("-");
                fetchRunTime();
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Callback<TMDBMovie> mMovieCallback = new Callback<TMDBMovie>() {
        @Override
        public void onResponse(Call<TMDBMovie> call, Response<TMDBMovie> response) {
            final String runtime = response.body().getRuntime();
            mMovie.setRuntime(runtime);
            mRuntimeView.setText(runtime + " min");
        }

        @Override
        public void onFailure(Call<TMDBMovie> call, Throwable t) {
            Toast.makeText(getContext(), "Failed to fetch movie runtime!", Toast.LENGTH_LONG).show();
        }
    };
}
