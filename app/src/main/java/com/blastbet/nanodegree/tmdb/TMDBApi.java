package com.blastbet.nanodegree.tmdb;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ilkka on 17.5.2016.
 */
public interface TMDBApi {
    @GET("movie/{sortorder}")
    void getMovieList(@Path("sortorder") String sortOrder, @Query("api_key") String apiKey);
}
