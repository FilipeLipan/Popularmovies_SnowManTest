package com.github.filipelipan.popularmovies.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.model.Movie;
import com.github.filipelipan.popularmovies.ui.MainActivity;

/**
 * Created by lispa on 09/04/2017.
 */

public class NotificationUtil {


    private static final int POPULAR_MOVIES_NOTIFICATION_ID = 30165;

    private static final String MOST_POPULAR_MOVIE_ID = "most_popular_movie_id";

    /**
     * Constructs and displays a notification for the newly updated movies data.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfNewPopularMovies(Context context, Movie movie) {

        if (movie != null) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int last_most_popular_movie_id = sharedPreferences.getInt(MOST_POPULAR_MOVIE_ID, 0);

            //if the most popular movie has changed show a notification
            //this if will make the notification less annoying
            if (last_most_popular_movie_id != movie.getId()) {

                //store the movie id so we can compare with the next movie
                SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
                sp.putInt(MOST_POPULAR_MOVIE_ID, movie.getId());
                sp.commit();

                String notificationTitle = movie.getOriginalTitle();
                String notificationText = context.getString(R.string.notificationText);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccentLight))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setAutoCancel(true);

                //create intent for the notification
                Intent startMainActivity = new Intent(context, MainActivity.class);

                //set up the pending intent for the notification
                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                taskStackBuilder.addNextIntentWithParentStack(startMainActivity);
                PendingIntent startMainActivityPendingIntent = taskStackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                //insert the pending intent
                notificationBuilder.setContentIntent(startMainActivityPendingIntent);


                NotificationManager notificationManager = (NotificationManager)
                        context.getSystemService(context.NOTIFICATION_SERVICE);

                notificationManager.notify(POPULAR_MOVIES_NOTIFICATION_ID, notificationBuilder.build());

            }
        }

    }

}
