package com.blastbet.nanodegree.tmdb;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ilkka on 17.5.2016.
 */
public interface TMDBApi {
    @GET("movie/{sortorder}")
    public Call<TMBDMovieListResponse> getMovieList(@Path("sortorder") String sortOrder, @Query("api_key") String apiKey);

    @GET("movie/{id}")
    public Call<TMDBMovie> getMovieDetails(@Path("id") String movieId, @Query("api_key") String apiKey);
}
