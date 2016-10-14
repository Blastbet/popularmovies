package com.blastbet.nanodegree.popularmovies.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by ilkka on 12.6.2016.
 */
public class MovieReview implements Parcelable{

    @Expose
    String id;

    @Expose
    String author;

    @Expose
    String content;

    public MovieReview(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public static final Parcelable.Creator<MovieReview> CREATOR =
            new Parcelable.Creator<MovieReview>() {
                public MovieReview createFromParcel(Parcel in) {
                    return new MovieReview(in);
                }
                public MovieReview[] newArray(int size) {
                    return new MovieReview[size];
                }
            };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
