package com.blastbet.nanodegree.popularmovies.tmdb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ilkka on 17.5.2016.
 */
public interface MovieApi {
    @GET("movie/{sortorder}")
    public Call<MovieList> getMovieList(@Path("sortorder") String sortOrder, @Query("api_key") String apiKey);

    @GET("movie/{id}")
    public Call<Movie> getMovieDetails(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    public Call<MovieTrailerList> getMovieTrailers(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    public Call<MovieReviewList> getMovieReviews(@Path("id") String movieId, @Query("api_key") String apiKey);
}
