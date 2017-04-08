package com.github.filipelipan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.github.filipelipan.popularmovies.data.MovieContract.MovieEntry;
import com.github.filipelipan.popularmovies.data.MovieContract.ClassificationEntry;

/**
 * Created by lispa on 18/02/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "moviesDbFilipeLipan.db";

    //version
    private static final int VERSION = 1;

    //Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create table classification
        final String CREATE_TABLE_CLASSIFICATION = "CREATE TABLE " + ClassificationEntry.TABLE_NAME + " (" +
                ClassificationEntry._ID                   + " INTEGER PRIMARY KEY, " +
                ClassificationEntry.COLUMN_NAME           + " TEXT NOT NULL);";

        //inserts for classification types
        final String INSERT_CLASSIFICATION_MOST_POPULAR = "insert into " + ClassificationEntry.TABLE_NAME +
                " ( " + ClassificationEntry._ID + "," + ClassificationEntry.COLUMN_NAME + ") " +
                " values (" + ClassificationEntry.MOST_POPULAR + ", '"+ ClassificationEntry.MOST_POPULAR_NAME +"');";

        final String INSERT_CLASSIFICATION_TOP_RATED = "insert into " + ClassificationEntry.TABLE_NAME +
                " ( " + ClassificationEntry._ID + "," + ClassificationEntry.COLUMN_NAME + ") " +
                " values (" + ClassificationEntry.TOP_RATED + ", '"+ ClassificationEntry.TOP_RATED_NAME +"');";

        final String INSERT_CLASSIFICATION_FAVORITES= "insert into " + ClassificationEntry.TABLE_NAME +
                " ( " + ClassificationEntry._ID + "," + ClassificationEntry.COLUMN_NAME + ") " +
                " values (" + ClassificationEntry.FAVORITES + ", '"+ ClassificationEntry.FAVORITES_NAME +"');";

        //create table movies
        final String CREATE_TABLE_MOVIES = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_ID_MOVIEDB    + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL, " +
                MovieEntry.COLUM_POSTER_PATH        + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION + " INTEGER NOT NULL, " +
                "FOREIGN KEY("+ MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION +") REFERENCES " +
                ClassificationEntry.TABLE_NAME + "("+ ClassificationEntry._ID +"));";


        sqLiteDatabase.execSQL(CREATE_TABLE_CLASSIFICATION);
        sqLiteDatabase.execSQL(INSERT_CLASSIFICATION_MOST_POPULAR);
        sqLiteDatabase.execSQL(INSERT_CLASSIFICATION_TOP_RATED);
        sqLiteDatabase.execSQL(INSERT_CLASSIFICATION_FAVORITES);
        sqLiteDatabase.execSQL(CREATE_TABLE_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
