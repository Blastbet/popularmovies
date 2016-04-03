package com.blastbet.nanodegree.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    // the fragment initialization parameter
    private static final String ARG_MOVIE = "movie_details";

    private Movie mMovie;

    private TextView mTitleView = null;
    private TextView mReleaseDateView = null;
    private TextView mOverviewView = null;
    private TextView mRuntimeView = null;
    private TextView mRatingView = null;
    private ImageView mPosterView = null;

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
            mTitleView.setText("---");
            mOverviewView.setText("---");
            mReleaseDateView.setText("---");
            mRuntimeView.setText("---");
            mRatingView.setText("---");
            mPosterView.setBackgroundColor(Color.GRAY);
        } else {
            mTitleView.setText(movie.getName());
            mOverviewView.setText(movie.getOverview());
            mReleaseDateView.setText(movie.getReleaseDate().toString());
            mRuntimeView.setText(movie.getRuntime());
            mRatingView.setText(movie.getRating().toString());
            Picasso.with(getActivity()).load(movie.getPosterImage())
                    .into(mPosterView);
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
