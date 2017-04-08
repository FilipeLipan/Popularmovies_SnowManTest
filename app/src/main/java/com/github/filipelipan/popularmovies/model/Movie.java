package com.github.filipelipan.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.filipelipan.popularmovies.data.MovieContract;

/**
 * Created by lispa on 08/01/2017.
 */

public class Movie implements Parcelable {

    public static final String PASSING_MOVIE = "passing_movie";
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    public int id;
    public String original_title;
    public String poster_path;
    public String overview;
    public double vote_average;
    public String release_date;

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.original_title = in.readString();
        this.poster_path = in.readString();
        this.overview = in.readString();
        this.vote_average = in.readDouble();
        this.release_date = in.readString();
    }


    /**
     *
     *  This method get a cursor from the content provider and a position
     *  and return a movie object
     * @param cursor cursor from the content provider
     * @param position the position of the required object
     * @return a movie object from the cursor
     */
    public static Movie recoverMovieFromCursor(Cursor cursor, int position) {
        Movie movie = new Movie();

        cursor.moveToPosition(position);

        movie.setId(getIntFromColumnName(cursor, MovieContract.MovieEntry.COLUMN_ID_MOVIEDB));
        movie.setOriginal_title(getStringFromColumnName(cursor, MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        movie.setPoster_path(getStringFromColumnName(cursor, MovieContract.MovieEntry.COLUM_POSTER_PATH));
        movie.setOverview(getStringFromColumnName(cursor, MovieContract.MovieEntry.COLUMN_OVERVIEW));
        movie.setVote_average(getFloatFromColumnName(cursor, MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        movie.setRelease_date(getStringFromColumnName(cursor, MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

        return movie;
    }

    /**
     * receive int from the cursor
     * @param cursor
     * @param columnName
     * @return return int from the selected column
     */
    private static int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    /**
     * receive String from the cursor
     * @param cursor
     * @param columnName
     * @return return String from the selected column
     */
    private static String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    /**
     * receive double from the cursor
     * @param cursor
     * @param columnName
     * @return return double from the selected column
     */
    private static double getFloatFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(columnIndex);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getYear(){
        String[] stringArray = new String[3];
        int i = 0;
        for(String text : release_date.split("-")){
            stringArray[i] = text;
            i++;
        }

        return stringArray[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.original_title);
        dest.writeString(this.poster_path);
        dest.writeString(this.overview);
        dest.writeDouble(this.vote_average);
        dest.writeString(this.release_date);
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
