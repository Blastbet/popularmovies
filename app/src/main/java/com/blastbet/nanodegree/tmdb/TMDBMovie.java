package com.blastbet.nanodegree.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by ilkka on 17.5.2016.
 */
public class TMDBMovie implements Parcelable{
    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    private boolean adult;

    @Expose
    private String overview;

    @SerializedName("release_date")
    @Expose
    private Date releaseDate;

    @SerializedName("genre_ids")
    private List<String> genreIds;

    @Expose
    private String id;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @Expose
    private String title;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @Expose
    private String popularity;

    @SerializedName("vote_count")
    @Expose
    private String voteCount;

    private boolean video;

    @SerializedName("vote_average")
    @Expose
    private String voteAverage;

    @SerializedName("runtime")
    @Expose
    private String runtime;

    /** Parcelable support code */
    public TMDBMovie(Parcel in) {
        this.posterPath = in.readString();
        this.id = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.runtime = in.readString();
        this.releaseDate = new Date(in.readLong());
        this.voteAverage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(runtime);
        dest.writeLong(releaseDate.getTime());
        dest.writeString(voteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TMDBMovie> CREATOR =
            new Parcelable.Creator<TMDBMovie>() {

                public TMDBMovie createFromParcel(Parcel in) {
                    return new TMDBMovie(in);
                }

                public TMDBMovie[] newArray(int size) {
                    return new TMDBMovie[size];
                }
            };


    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
}
