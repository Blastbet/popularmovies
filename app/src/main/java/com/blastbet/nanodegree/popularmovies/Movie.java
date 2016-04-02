package com.blastbet.nanodegree.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by ilkka on 30.3.2016.
 */

public class Movie implements Parcelable {
    protected Uri posterImage;
    protected String id;
    protected String name;
    protected String overview;
    protected Date releaseDate;
    protected Rating rating;

    public Movie(Uri posterImage, String id, String name, String overview, Date releaseDate, Rating rating) {
        this.posterImage = posterImage;
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public Movie(Uri posterImage, String id, String name, String overview, Date releaseDate, String averageVote, String voteCount) {
        this.posterImage = posterImage;
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = new Rating(averageVote, voteCount);
    }

    /** Parcelable support code */
    public Movie(Parcel in) {
        this.posterImage = Uri.parse(in.readString());
        this.id = in.readString();
        this.name = in.readString();
        this.overview = in.readString();
        this.releaseDate = new Date(in.readLong());
        this.rating = in.readParcelable(Rating.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterImage.toString());
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(overview);
        dest.writeLong(releaseDate.getTime());
        dest.writeParcelable(rating, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR =
        new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /** Getters & Setters for the members */
    public Uri getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(Uri posterImage) {
        this.posterImage = posterImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    static class Rating implements Parcelable {
        protected String averageVote;
        protected String voteCount;

        public Rating(String averageVote, String voteCount) {
            this.averageVote = averageVote;
            this.voteCount = voteCount;
        }

        /** Parcelable support code */
        public Rating(Parcel in) {
            this.averageVote = in.readString();
            this.voteCount = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(averageVote);
            dest.writeString(voteCount);
        }

        public static final Parcelable.Creator<Rating> CREATOR =
            new Parcelable.Creator<Rating>() {

            public Rating createFromParcel(Parcel in) {
                return new Rating(in);
            }

            public Rating[] newArray(int size) {
                return new Rating[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        /** Getters and setters for member variables */
        public String getAverageVote() {
            return averageVote;
        }

        public void setAverageVote(String averageVote) {
            this.averageVote = averageVote;
        }

        public String getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(String voteCount) {
            this.voteCount = voteCount;
        }

        /** Override toString */
        @Override
        public String toString() {
            return averageVote + "/10 (" + voteCount + " votes)";
        }
    }
}
