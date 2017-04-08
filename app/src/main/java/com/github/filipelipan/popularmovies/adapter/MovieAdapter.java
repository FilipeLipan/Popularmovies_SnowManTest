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

    public MovieAdapter(Cursor cursor, Context context) {
        mContext = context;
        mCursor = cursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
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
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_list_item_poster);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {

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
