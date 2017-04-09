package com.github.filipelipan.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.data.MovieContract;
import com.github.filipelipan.popularmovies.model.Movie;
import com.github.filipelipan.popularmovies.moviedb.MoviesDbService;
import com.github.filipelipan.popularmovies.ui.MainActivity;
import com.github.filipelipan.popularmovies.ui.fragments.DetailFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by lispa on 19/02/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private Context mContext;
    private Cursor mCursor;
    private TextView mEmptyTextView;

    public MovieAdapter(Cursor cursor, Context context) {
        mContext = context;
        mCursor = cursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * set an empty view to be show when the recyclerView is empty
     * @param textView empty TextView that will be shown when the recyclerView is empty
     */
    public void setEmptyTextView(TextView textView){
        mEmptyTextView = textView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            if(mEmptyTextView != null) {
                if (mCursor.getCount() == 0) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTextView.setVisibility(View.INVISIBLE);
                }
            }
            return mCursor.getCount();
        }

        if(mEmptyTextView != null){
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_list_item_poster);

            itemView.setOnClickListener(this);
        }

        private void bindView(int position) {

            int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUM_POSTER_PATH);
            mCursor.moveToPosition(position);
            String posterPath = mCursor.getString(posterPathIndex);

            String imgUrl = MoviesDbService.BASE_POSTER_URL + posterPath;
            Picasso.with(mContext)
                    .load(imgUrl)
                    .placeholder(VectorDrawableCompat.create(mContext.getResources(), R.drawable.load, null))
                    .error(R.drawable.error)
                    .into(mImageView);
        }

        @Override
        public void onClick(View view) {

            //if it is a table, set the variable movie inside the fragment
            //else create a new fragment and send the selected movie trough a bundle
            if (mContext.getResources().getBoolean(R.bool.is_tablet)) {
                DetailFragment detailFragment = (DetailFragment) ((FragmentActivity) mContext)
                        .getSupportFragmentManager().findFragmentByTag(MainActivity.DETAIL_FRAGMENT);

                Movie movie = Movie.recoverMovieFromCursor(mCursor, getAdapterPosition());

                detailFragment.setUp(movie, true);

            } else {

                DetailFragment fragment = new DetailFragment();

                Bundle bundle = new Bundle();
                bundle.putParcelable(Movie.PASSING_MOVIE, Movie.recoverMovieFromCursor(mCursor, getAdapterPosition()));
                fragment.setArguments(bundle);

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ph_main_activity, fragment, MainActivity.DETAIL_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

}
