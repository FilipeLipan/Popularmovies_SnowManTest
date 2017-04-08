package com.github.filipelipan.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.github.filipelipan.popularmovies.data.MovieContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by lispa on 21/02/2017.
 */

public class PopularMoviesSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    public static final String POPULAR_MOVIES_SYNC_TAG = "popular-movies-sync";

    /**
     * Schedules a repeating sync of movies from the webservice data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Creating the job that will sync popular movies*/
        Job syncPopularMoviesJob = dispatcher.newJobBuilder()
                .setService(DownloadMoviesFirebaseJobService.class)
                .setTag(POPULAR_MOVIES_SYNC_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncPopularMoviesJob);
    }


    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;

                Cursor cursor = context.getContentResolver().query(uri,
                        null,
                        null,
                        null,
                        null);

                if(cursor == null || cursor.getCount() == 0){
                    startImmediateSync(context);
                }

                cursor.close();
            }
        });

        checkForEmpty.start();
    }


    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, PopularMoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
