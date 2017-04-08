package com.github.filipelipan.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lispa on 21/01/2017.
 */

public class Review implements Parcelable {
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    public String author;
    public String content;
    public String url;

    public Review() {
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }
}
