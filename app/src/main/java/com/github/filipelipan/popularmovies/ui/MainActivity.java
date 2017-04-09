package com.github.filipelipan.popularmovies.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.broadcastreceiver.NetworkConnectReceiver;
import com.github.filipelipan.popularmovies.event.RetrofitFinishLoadEvent;
import com.github.filipelipan.popularmovies.sync.PopularMoviesSyncUtils;
import com.github.filipelipan.popularmovies.ui.fragments.DetailFragment;
import com.github.filipelipan.popularmovies.ui.fragments.GridMoviesFragment;
import com.github.filipelipan.popularmovies.util.EventBus;
import com.github.filipelipan.popularmovies.util.OperationType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String GRID_MOVIE_FRAGMENT = "grid_movie_fragment";
    public static final String DETAIL_FRAGMENT = "detail_fragment";

    private GridMoviesFragment mGridMoviesFragment;
    private DetailFragment mDetailFragment;
    private NetworkConnectReceiver mReceiver = new NetworkConnectReceiver();
    private boolean mConnectionHasChanged = false;

    private boolean mIsFirstTimeOpen = true;
    private static final String KEY_IS_FIRST_TIME_OPEN = "key_first_time_open";

    @BindView(R.id.main_activity_snackbar_place_holder)
    RelativeLayout mRelativeLayout;

    @Override
    protected void onStart() {
        super.onStart();

        //register the activity to receive an event when retrofit finishes loading
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState !=null ){
            if(savedInstanceState.containsKey(KEY_IS_FIRST_TIME_OPEN)){
                mIsFirstTimeOpen = savedInstanceState.getBoolean(KEY_IS_FIRST_TIME_OPEN);
            }
        }

        CustomActivityOnCrash.install(this);

        if(mIsFirstTimeOpen){
            checkPlayServices();
        }

        // is tablet receive true if the smaller screen is equal to 600dp
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        //initialize the jobDispatcher and starts syncing
        PopularMoviesSyncUtils.initialize(this);


        /*
        * if it is a table the main activity will create two fragments and display both
        * if it is a phone this activity will show one fragment
        */
        if (isTablet) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main_activity);
            setSupportActionBar(toolbar);

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                    .findFragmentByTag(GRID_MOVIE_FRAGMENT);
            if (mGridMoviesFragment == null) {
                mGridMoviesFragment = new GridMoviesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_movies_fragment_tablet, mGridMoviesFragment, GRID_MOVIE_FRAGMENT);
                fragmentTransaction.commit();
            }

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
            if (mDetailFragment == null) {
                mDetailFragment = new DetailFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_detail_fragment_tablet, mDetailFragment, DETAIL_FRAGMENT);
                fragmentTransaction.commit();
            }

        } else {

            /*
            * if the fragment already exist, i got him with savedFragment, but if don't i create a new one
            */
            mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                    .findFragmentByTag(GRID_MOVIE_FRAGMENT);
            if (mGridMoviesFragment == null) {
                mGridMoviesFragment = new GridMoviesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.ph_main_activity, mGridMoviesFragment, GRID_MOVIE_FRAGMENT);
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        *  register a receiver to warn the user about the connection
        */
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);

        IntentFilter customFilter = new IntentFilter(NetworkConnectReceiver.NOTIFY_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, customFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister inside onPause, preventing the receiver to live outside our app
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unregister the activity to receive an event when retrofit finishes loading
        EventBus.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsFirstTimeOpen = false;
        outState.putBoolean(KEY_IS_FIRST_TIME_OPEN, mIsFirstTimeOpen);
    }

    /**
     * This method will be call when retrofit finishes, it will make gridMoviesFragment load the new data
     *
     * @param event a class that store messages coming from the event
     */
    @Subscribe
    public void onRetrofitFinishLoading(RetrofitFinishLoadEvent event){

        //reset GridMoviesFragment loader
        mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                .findFragmentByTag(GRID_MOVIE_FRAGMENT);
        mGridMoviesFragment.loadMovies(OperationType.MOST_POPULAR);
    }

    /**
     * declare a local receiver to warn the user about the connection status
     */
    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(NetworkConnectReceiver.EXTRA_IS_CONNECTED, false);
            if (isConnected) {

                /**
                 * mConnectionHasChanged make sure that the receiver will only show the connection
                 * message if the device don't have a connection
                 */
                if(mConnectionHasChanged) {
                    Snackbar.make(mRelativeLayout, R.string.network_connected, Snackbar.LENGTH_LONG).show();
                    mConnectionHasChanged = false;
                }
            } else {
                Snackbar.make(mRelativeLayout, R.string.network_disconnected, Snackbar.LENGTH_INDEFINITE).show();
                mConnectionHasChanged = true;
            }
        }
    };

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (getResources().getBoolean(R.bool.is_tablet)) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mGridMoviesFragment = (GridMoviesFragment) getSupportFragmentManager()
                .findFragmentByTag(GRID_MOVIE_FRAGMENT);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.it_most_popular:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.MOST_POPULAR);
                }
                return true;
            case R.id.it_top_rated:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.TOP_RATED);
                }
                return true;
            case R.id.it_favorites:
                if (mGridMoviesFragment != null) {
                    mGridMoviesFragment.loadMovies(OperationType.FAVORITE);
                }
                return true;
        }
        return false;
    }
}
