package com.blastbet.nanodegree.popularmovies.tmdb;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ilkka on 12.6.2016.
 */
public class MovieReviewList {
    @Expose
    String id;

    @Expose
    int page;

    @SerializedName("results")
    @Expose
    List<MovieReview> movieReviews;

    @SerializedName("total_pages")
    @Expose
    int totalPages;

    @SerializedName("total_results")
    @Expose
    int totalResults;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
