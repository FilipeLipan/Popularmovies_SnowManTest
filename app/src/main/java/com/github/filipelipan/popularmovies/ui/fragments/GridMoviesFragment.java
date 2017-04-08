package com.github.filipelipan.popularmovies.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.adapter.MovieAdapter;
import com.github.filipelipan.popularmovies.data.MovieContract;
import com.github.filipelipan.popularmovies.model.Movie;
import com.github.filipelipan.popularmovies.ui.MainActivity;
import com.github.filipelipan.popularmovies.util.OperationType;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by lispa on 27/01/2017.
 */

public class GridMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int ID_POPULAR_MOVIES_LOADER = 50;
    public static final String KEY_OPERATION_TYPE = "key_operation_type";
    private static final String TAG = GridMoviesFragment.class.getSimpleName();
    private static final String KEY_IS_FIRST_TIME_OPEN = "key_first_time_open";


    @BindView(R.id.gv_main) RecyclerView mGridView;
    @BindView(R.id.tv_main_grid_empty) TextView mEmptyTextView;

    private MovieAdapter mMovieAdapter;
    private Context mContext;
    private Toolbar mToolbar;
    private boolean mIsFirstTimeOpen = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    // loader that loads movies from the content provider
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int mOption = 2;
        if(args != null) {
            mOption = args.getInt(KEY_OPERATION_TYPE);
        }
        final int finalMOption = mOption;

        return new AsyncTaskLoader<Cursor>(mContext) {
            Cursor mMoviesData;

            @Override
            protected void onStartLoading() {
                if(mMoviesData == null){
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI_MOVIES_BY_CLASSIFICATION;
                    uri = uri.buildUpon().appendEncodedPath(finalMOption + "").build();

                    mMoviesData = mContext.getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);
                    return mMoviesData;
                }catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if(data.getCount() > 0) {
            if (mContext.getResources().getBoolean(R.bool.is_tablet)) {
                DetailFragment detailFragment = (DetailFragment) ((FragmentActivity) mContext)
                        .getSupportFragmentManager().findFragmentByTag(MainActivity.DETAIL_FRAGMENT);

                if(mIsFirstTimeOpen) {
                    Movie movie = Movie.recoverMovieFromCursor(data, 0);
                    detailFragment.setUp(movie, true);
                    mIsFirstTimeOpen = false;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        if(savedInstanceState !=null ){
            if(savedInstanceState.containsKey(KEY_IS_FIRST_TIME_OPEN)){
                mIsFirstTimeOpen = savedInstanceState.getBoolean(KEY_OPERATION_TYPE);
            }
        }


        View view = inflater.inflate(R.layout.grid_movies_fragment, container, false);
        ButterKnife.bind(this,view);


        if(!mContext.getResources().getBoolean(R.bool.is_tablet)){
            mToolbar = (Toolbar) view.findViewById(R.id.tb_movies_fragment_activity);
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            setHasOptionsMenu(true);
        }


        mMovieAdapter = new MovieAdapter(null, mContext);
        mGridView.setLayoutManager(new GridLayoutManager(mContext, calculateNoOfColumns(mContext)));
        mMovieAdapter.setEmptyTextView(mEmptyTextView);
        mGridView.setAdapter(mMovieAdapter);

        Bundle bundle = new Bundle();
        bundle.putInt(KEY_OPERATION_TYPE, OperationType.MOST_POPULAR);
        ((FragmentActivity)mContext).getSupportLoaderManager().initLoader(ID_POPULAR_MOVIES_LOADER, bundle, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                ((Activity) mContext).onBackPressed();
                return true;
            case R.id.it_most_popular:

                    loadMovies(OperationType.MOST_POPULAR);

                return true;
            case R.id.it_top_rated:

                    loadMovies(OperationType.TOP_RATED);

                return true;
            case R.id.it_favorites:
                    loadMovies(OperationType.FAVORITE);

                return true;
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.it_favorites);
        item.setVisible(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_FIRST_TIME_OPEN, mIsFirstTimeOpen);
    }


    /**
     * Calculate the number of columns for the Gridview
     *
     * @param context Used to access the DisplayMetrics
     * @return An int resulting from the division between the screen width and a given dp.
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = 0;

        if(context.getResources().getBoolean(R.bool.is_tablet)){
            noOfColumns = (int) (dpWidth / 480);
        }else {
            noOfColumns = (int) (dpWidth / 180);
        }

        if(noOfColumns <= 0){
            noOfColumns = 1;
        }
        return noOfColumns;
    }

    public void loadMovies(int option){
        Bundle bundle = new Bundle();
        bundle.putInt(GridMoviesFragment.KEY_OPERATION_TYPE, option);
        ((FragmentActivity)mContext).getSupportLoaderManager().restartLoader(ID_POPULAR_MOVIES_LOADER, bundle, this);
    }
}
