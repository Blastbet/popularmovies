package com.blastbet.nanodegree.popularmovies.tmdb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ilkka on 12.6.2016.
 */
public class MovieTrailerList {
    @Expose
    String id;

    @SerializedName("results")
    @Expose
    List<MovieTrailer> movieTrailers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
    }
}
