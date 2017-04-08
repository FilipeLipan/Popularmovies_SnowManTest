package com.github.filipelipan.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

import com.github.filipelipan.popularmovies.util.OperationType;

/**
 * Created by lispa on 21/02/2017.
 */

public class PopularMoviesSyncIntentService extends IntentService {
    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MovieTasks.excuteTask(this, MovieTasks.ACTION_DOWNLOAD_MOST_POPULAR_MOVIES);
        MovieTasks.excuteTask(this, MovieTasks.ACTION_DOWNLOAD_TOP_RATED_MOVIES);
    }
}
