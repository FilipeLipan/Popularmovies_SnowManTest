package com.github.filipelipan.popularmovies.sync;


import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by lispa on 21/02/2017.
 */

public class DownloadMoviesFirebaseJobService extends JobService{


    private AsyncTask<Void, Void, Void> mFetchMoviesTask;

    /**
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchMoviesTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = getApplicationContext();
                MovieTasks.excuteTask(context, MovieTasks.ACTION_DOWNLOAD_MOST_POPULAR_MOVIES);
                MovieTasks.excuteTask(context, MovieTasks.ACTION_DOWNLOAD_TOP_RATED_MOVIES);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                jobFinished(job, false);
            }
        };

        mFetchMoviesTask.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchMoviesTask != null){
            mFetchMoviesTask.cancel(true);
        }
        return true;
    }
}
