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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;


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

    private Movie mMovie;

    private TextView mTitleView = null;
    private TextView mReleaseDateView = null;
    private TextView mOverviewView = null;
    private TextView mRuntimeView = null;
    private TextView mRatingView = null;
    private ImageView mPosterView = null;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy");

    private MoviePosterLoader mMoviePosterLoader = null;
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
    public static MovieDetailsFragment newInstance(Movie movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        final Movie movie = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_extra));
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
            mTitleView.setText(movie.getName());
            mOverviewView.setText(movie.getOverview());
            final String releaseDate = mDateFormat.format(movie.getReleaseDate()).toString();
            mReleaseDateView.setText(releaseDate);
            mRuntimeView.setText(movie.getRuntime());
            mRatingView.setText(movie.getRating().toString());
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
                    Log.e(LOG_TAG, "Failed to load poster image for movie \"" + mMovie.getName() +"\"");
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
}
