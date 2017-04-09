package com.github.filipelipan.popularmovies.ui.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.adapter.ReviewAdapter;
import com.github.filipelipan.popularmovies.adapter.TrailerAdapter;
import com.github.filipelipan.popularmovies.data.MovieContract;
import com.github.filipelipan.popularmovies.model.Movie;
import com.github.filipelipan.popularmovies.model.Result;
import com.github.filipelipan.popularmovies.model.Review;
import com.github.filipelipan.popularmovies.model.Reviews;
import com.github.filipelipan.popularmovies.model.Trailer;
import com.github.filipelipan.popularmovies.moviedb.MoviesDbService;
import com.github.filipelipan.popularmovies.util.NetworkUtil;
import com.github.filipelipan.popularmovies.util.OperationType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lispa on 28/01/2017.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = DetailFragment.class.getSimpleName();

    public static final int ID_DETAIL_FRAGMENT_LOADER = 16515616;

    public static final String KEY_OPERATION_TYPE = "key_operation_type";
    public static final String KEY_MOVIE_SAVED_INSTANCE = "key-movie-saved-instance";
    public static final String KEY_REVIEWS_SAVED_INSTANCE = "key-reviews-saved-instance";
    public static final String KEY_TRAILERS_SAVED_INSTANCE = "key-trailers-saved-instance";

    @BindView(R.id.tv_year)         TextView mTextViewDate;
    @BindView(R.id.tv_overview)     TextView mTextViewOverView;
    @BindView(R.id.tv_vote_average) TextView mTextViewVoteAverage;
    @BindView(R.id.tv_duration)     TextView mTextViewDuration;
    @BindView(R.id.iv_poster)       ImageView mImageViewPoster;
    @BindView(R.id.rv_detail_trailers)  RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.rv_detail_reviews)   RecyclerView mRecyclerViewReviews;
    @BindView(R.id.ct_detail_toolbar)   CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.image)           ImageView mImageView;
    @BindView(R.id.fab_favorite_selected)   FloatingActionButton mFABFavoriteSelected;
    @BindView(R.id.fab_favorite_unselected) FloatingActionButton mFABFavoriteUnselected;
    @BindView(R.id.pb_detail_trailers) ProgressBar mProgressBarTrailers;
    @BindView(R.id.pb_detail_reviews) ProgressBar mProgressBarReviews;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_detail_empty_reviews) TextView mEmptyTextViewReviews;
    @BindView(R.id.tv_detail_empty_trailers) TextView mEmptyTextViewTrailers;

    private Movie mMovie;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int idMovie = 2;

        if(args != null) {
            idMovie = args.getInt(KEY_OPERATION_TYPE);
        }
        final int mId = idMovie;

        return new AsyncTaskLoader<Cursor>(mContext) {
            Cursor mFavorites;

            @Override
            protected void onStartLoading() {
                if(mFavorites == null){
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI_MOVIES_BY_CLASSIFICATION;
                    uri = uri.buildUpon().appendEncodedPath(OperationType.FAVORITE + "")
                            .appendEncodedPath(mId + "").build();

                    mFavorites = mContext.getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);
                    return mFavorites;
                }catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load favorite.");
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() > 0){
            mFABFavoriteSelected.setVisibility(View.VISIBLE);
            mFABFavoriteUnselected.setVisibility(View.INVISIBLE);
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        ButterKnife.bind(this, view);

        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_REVIEWS_SAVED_INSTANCE)){
                mReviews = savedInstanceState.getParcelableArrayList(KEY_REVIEWS_SAVED_INSTANCE);
            }

            if(savedInstanceState.containsKey(KEY_TRAILERS_SAVED_INSTANCE)){
                mTrailers = savedInstanceState.getParcelableArrayList(KEY_TRAILERS_SAVED_INSTANCE);
            }

            if(savedInstanceState.containsKey(KEY_MOVIE_SAVED_INSTANCE)){
                mMovie = savedInstanceState.getParcelable(KEY_MOVIE_SAVED_INSTANCE);
                setUp(mMovie, false);
            }

            savedInstanceState.clear();
        }

        Bundle bundle = this.getArguments();

        if(!mContext.getResources().getBoolean(R.bool.is_tablet)) {

            // if its a phone get the bundle an set the movie
            if(bundle != null) {
                if (bundle.containsKey(Movie.PASSING_MOVIE)) {
                    mMovie = bundle.getParcelable(Movie.PASSING_MOVIE);
                    setUp(mMovie, true);
                }
            }

            // if it is a phone show the detail menus and set setDisplayHomeAsUpEnabled
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);

        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE_SAVED_INSTANCE, mMovie);
        outState.putParcelableArrayList(KEY_REVIEWS_SAVED_INSTANCE, mReviews);
        outState.putParcelableArrayList(KEY_TRAILERS_SAVED_INSTANCE, mTrailers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ((Activity) mContext).onBackPressed();
                return true;

        }
        return false;
    }

    /**
     *  set up the views with information deliver by the movie parameter, reloadReviewsAndTrailers
     *  will decide if there's is need to reload the trailers and reviews, to avoid unnecessary
     *  downloads when android changes between lifecycle's
     *
     * @param movie this variable will be use to set up the fragment
     * @param reloadReviewsAndTrailers if true the fragment will download the trailers and reviews
     *                                  again, otherwise the method will use the trailers and reviews
     *                                 from the member variables
     */
    public void setUp(Movie movie, boolean reloadReviewsAndTrailers){
        mMovie = movie;
        if(mMovie != null) {
            mFABFavoriteSelected.setVisibility(View.INVISIBLE);
            mFABFavoriteUnselected.setVisibility(View.VISIBLE);

            mTextViewDate.setText(mMovie.getYear());
            mTextViewOverView.setText(mMovie.getOverview());
            mTextViewVoteAverage.setText(mMovie.getVoteAverage() + "/10");
            mTextViewDuration.setText("120m");

            mCollapsingToolbarLayout.setTitle(mMovie.getOriginalTitle());

            String imgUrl = MoviesDbService.BASE_POSTER_URL + mMovie.getPosterPath();
            String imgUrlHD = MoviesDbService.BASE_POSTER_URL_HD + mMovie.getPosterPath();

            Picasso.with(mContext)
                    .load(imgUrlHD)
                    .placeholder(VectorDrawableCompat.create(getResources(), R.drawable.load, null))
                    .error(R.drawable.error)
                    .into(mImageView);

            Picasso.with(mContext)
                    .load(imgUrl)
                    .placeholder(VectorDrawableCompat.create(getResources(), R.drawable.load, null))
                    .error(R.drawable.error)
                    .into(mImageViewPoster);

            mTrailerAdapter = new TrailerAdapter(mTrailers, mContext);
            LinearLayoutManager linearLayoutManagerTrailers = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mTrailerAdapter.setEmptyTextView(mEmptyTextViewTrailers);

            LinearLayoutManager linearLayoutManagerReviews = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            mRecyclerViewTrailers.setLayoutManager(linearLayoutManagerTrailers);
            mRecyclerViewTrailers.setAdapter(mTrailerAdapter);

            mReviewAdapter = new ReviewAdapter(mReviews, mContext);
            mRecyclerViewReviews.setLayoutManager(linearLayoutManagerReviews);
            mReviewAdapter.setEmptyTextView(mEmptyTextViewReviews);
            mRecyclerViewReviews.setAdapter(mReviewAdapter);

            if(reloadReviewsAndTrailers) {
                receiveTrailers(mMovie.getId());
                receiveReviews(mMovie.getId());
            }else {
                mTrailerAdapter.setTrailers(mTrailers);
                mReviewAdapter.setReviews(mReviews);
            }

            //start the favorite loader
            Bundle bundleMovieId = new Bundle();
            bundleMovieId.putInt(KEY_OPERATION_TYPE, mMovie.getId());
            LoaderManager loaderManager = ((FragmentActivity)mContext).getSupportLoaderManager();
            Loader<String> movieFavoriteLoader = loaderManager.getLoader(ID_DETAIL_FRAGMENT_LOADER);
            if(movieFavoriteLoader == null){
                loaderManager.initLoader(ID_DETAIL_FRAGMENT_LOADER, bundleMovieId, this);
            }else {
                loaderManager.restartLoader(ID_DETAIL_FRAGMENT_LOADER, bundleMovieId, this);
            }
        }
    }

    /**
     *  Receive a ArrayList of Trailers from the webservice and display in the respective RecyclerView
     * @param id this id comes from the moviesdb webservice
     */
    private void receiveTrailers(int id) {
        mProgressBarTrailers.setVisibility(View.VISIBLE);
        if (NetworkUtil.isConnectionAvailable(mContext)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MoviesDbService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoviesDbService service = retrofit.create(MoviesDbService.class);
            Call<Result> makeTrailersRequest;
            makeTrailersRequest = service.getTrailers(id,MoviesDbService.API_KEY);

            makeTrailersRequest.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (!response.isSuccessful() || response == null) {
                        Log.e(TAG, getString(R.string.retrofitError));
                    } else {
                        if(response.body() != null) {
                            Result result = response.body();
                            mTrailers = result.getYoutubeTrailers();
                            mTrailerAdapter.setTrailers(mTrailers);
                            mProgressBarTrailers.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.e(TAG, getString(R.string.retrofitError));
                }
            });
        }

    }

    /**
     *  Receive a ArrayList of Review from the webservice and display in the respective RecyclerView
     * @param id this id comes from the moviesdb webservice
     */
    private void receiveReviews(int id) {
        mProgressBarReviews.setVisibility(View.VISIBLE);
        if (NetworkUtil.isConnectionAvailable(mContext)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MoviesDbService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MoviesDbService service = retrofit.create(MoviesDbService.class);
            Call<Reviews> makeTrailersRequest;
            makeTrailersRequest = service.getReviews(id,MoviesDbService.API_KEY);

            makeTrailersRequest.enqueue(new Callback<Reviews>() {
                @Override
                public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                    if (!response.isSuccessful() || response == null) {
                        Log.e(TAG, getString(R.string.retrofitError));
                    } else {
                        if(response.body() != null) {
                            Reviews reviews = response.body();
                            mReviews = reviews.getResults();
                            mReviewAdapter.setReviews(mReviews);

                        }
                        mProgressBarReviews.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Reviews> call, Throwable t) {
                    Log.e(TAG, getString(R.string.retrofitError));
                }
            });
        }
    }

    @OnClick(R.id.fab_favorite_unselected)
    public void favorited(){
        toggleFavoriteFAB();

        ContentValues contentValues = buildContentValuesFromMovie(mMovie, OperationType.FAVORITE);

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        mContext.getContentResolver().insert(uri, contentValues);
    }

    @OnClick(R.id.fab_favorite_selected)
    public void unFavorited(){
        toggleFavoriteFAB();

        Uri uri = MovieContract.MovieEntry.CONTENT_URI_MOVIES_BY_CLASSIFICATION;
        uri = uri.buildUpon().appendEncodedPath(OperationType.FAVORITE + "")
                .appendEncodedPath(mMovie.getId() + "").build();

        mContext.getContentResolver().delete(uri,
                null,
                null);
    }

    //toggle progressBar visibility
    private void toggleFavoriteFAB(){
        if(mFABFavoriteSelected.getVisibility() == View.VISIBLE){
            mFABFavoriteSelected.setVisibility(View.INVISIBLE);
            mFABFavoriteUnselected.setVisibility(View.VISIBLE);
        }else {
            mFABFavoriteSelected.setVisibility(View.VISIBLE);
            mFABFavoriteUnselected.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Transform a movies into a ContentValues, the classification column will be fill by a constant
     * from OperationType
     *
     * @param movie One Movie object
     * @param option a constant from OperationType
     * @return ContentValues object
     */
    private static ContentValues buildContentValuesFromMovie(Movie movie, int option) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID_MOVIEDB, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUM_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION, option);

        return contentValues;
    }

}
