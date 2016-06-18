package com.blastbet.nanodegree.popularmovies.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by ilkka on 12.6.2016.
 */
public class MovieTrailer implements Parcelable {
    @Expose
    String id;

    @Expose
    String key;

    @Expose
    String name;

    @Expose
    String site;

    @Expose
    int size;

    @Expose
    String type;

    public MovieTrailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Parcelable.Creator<MovieTrailer> CREATOR =
            new Parcelable.Creator<MovieTrailer>() {
                public MovieTrailer createFromParcel(Parcel in) {
                    return new MovieTrailer(in);
                }
                public MovieTrailer[] newArray(int size) {
                    return new MovieTrailer[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
