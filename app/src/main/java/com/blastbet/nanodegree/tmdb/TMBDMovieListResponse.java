package com.blastbet.nanodegree.tmdb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ilkka on 21.5.2016.
 */
public class TMBDMovieListResponse {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String page;

    @SerializedName("results")
    @Expose
    private List<TMDBMovie> movieList;

    @SerializedName("total_results")
    private String totalResults;

    @SerializedName("total_pages")
    private String totalPages;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public List<TMDBMovie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<TMDBMovie> movieList) {
        this.movieList = movieList;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public static TMBDMovieListResponse parseJSON(String response) {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(DATE_FORMAT);
        Gson gson = builder.create();
        TMBDMovieListResponse movieListResponse = gson.fromJson(response, TMBDMovieListResponse.class);
        return movieListResponse;
    }
}
