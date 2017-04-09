package com.github.filipelipan.popularmovies.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.ui.MainActivity;

/**
 * Created by lispa on 09/04/2017.
 */

public class NotificationUtil {


    private static final int POPULAR_MOVIES_NOTIFICATION_ID = 30165;

    /**
     * Constructs and displays a notification for the newly updated movies data.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfNewPopularMovies(Context context){

        //TODO: get a movie from the content provider
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = "Your movies were updated";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorAccentLight))
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
