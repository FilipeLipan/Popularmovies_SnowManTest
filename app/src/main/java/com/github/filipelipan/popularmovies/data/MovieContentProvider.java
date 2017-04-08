package com.github.filipelipan.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.github.filipelipan.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by lispa on 19/02/2017.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_BY_CLASSIFICATION = 101;
    public static final int CODE_MOVIES_BY_CLASSIFICATION_WITH_ID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIES);

        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_CLASSIFICATION + "/#", CODE_MOVIES_BY_CLASSIFICATION);

        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_CLASSIFICATION + "/#/#", CODE_MOVIES_BY_CLASSIFICATION_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_MOVIES_BY_CLASSIFICATION:
                String id = uri.getPathSegments().get(2);

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION + " = ?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_MOVIES_BY_CLASSIFICATION_WITH_ID:
                String classificationId = uri.getPathSegments().get(2);
                String moviesDbId = uri.getPathSegments().get(3);

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION + " = ? AND " +
                                MovieEntry.COLUMN_ID_MOVIEDB + " = ?",
                        new String[]{classificationId,moviesDbId},
                        null,
                        null,
                        sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("Not implemented.");
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:

                long id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted = 0;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME, null, null);
                break;
            case CODE_MOVIES_BY_CLASSIFICATION:
                String id = uri.getPathSegments().get(2);
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION + "=?", new String[]{id});
                break;
            case CODE_MOVIES_BY_CLASSIFICATION_WITH_ID:
                String classificationId = uri.getPathSegments().get(2);
                String moviesDbId = uri.getPathSegments().get(3);

                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,
                        MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION + " = ? AND " +
                                MovieEntry.COLUMN_ID_MOVIEDB + " = ?",
                        new String[]{classificationId,moviesDbId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (numRowsDeleted != 0) {
            // A movie was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return  numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new RuntimeException("Not implemented.");
    }
}
