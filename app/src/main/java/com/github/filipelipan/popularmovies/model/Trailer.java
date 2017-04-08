package com.github.filipelipan.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lispa on 21/01/2017.
 */

public class Trailer implements Parcelable {
    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
    public String name;
    public String source;

    public Trailer() {
    }

    protected Trailer(Parcel in) {
        this.name = in.readString();
        this.source = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.source);
    }
}
