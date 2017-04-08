package com.github.filipelipan.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.github.filipelipan.popularmovies.util.OperationType;

/**
 * Created by lispa on 18/02/2017.
 */

public class MovieContract {

        /* Add content provider constants to the Contract
     Clients need to know how to access the task data, and it's your job to provide
     these content URI's for the path to that data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the tasks directory
        4) Content URI for data in the TaskEntry class
      */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.github.filipelipan.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "movies" directory
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_CLASSIFICATION  = "classification";


    /* MovieEntry is an inner class that defines the contents of the movies table */
    public static final class MovieEntry implements BaseColumns{

        //MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final Uri CONTENT_URI_MOVIES_BY_CLASSIFICATION =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_CLASSIFICATION).build();


        //Movies table and column names
        public static final String TABLE_NAME = "movies";

        //columns
        public static final String COLUMN_ID_MOVIEDB = "id_moviedb";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUM_POSTER_PATH = "post_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FOREIGN_KEY_CLASSIFICATION = "fk_id_classification";
    }

    /* ClassificationEntry is an inner class that defines the contents of the classification table */
    public static final class ClassificationEntry implements BaseColumns{

        //Classification table and column names
        public static final String TABLE_NAME = "classification";

        //columns
        public static final String COLUMN_NAME = "name";

        //classification types
        public static final int MOST_POPULAR = OperationType.MOST_POPULAR;
        public static final int TOP_RATED = OperationType.TOP_RATED;
        public static final int FAVORITES = OperationType.FAVORITE;

        public static final String MOST_POPULAR_NAME = "most_popular";
        public static final String TOP_RATED_NAME = "top_rated";
        public static final String FAVORITES_NAME = "favorites_name";
    }
}
