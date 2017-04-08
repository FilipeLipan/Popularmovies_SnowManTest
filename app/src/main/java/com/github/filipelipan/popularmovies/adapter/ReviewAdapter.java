package com.github.filipelipan.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Created by lispa on 21/01/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> mReviews;
    private Context mContext;
    private boolean isExpanded = true;
    private TextView mEmptyTextView;

    public ReviewAdapter(ArrayList<Review> reviews, Context context){
        mReviews = reviews;
        mContext = context;
    }

    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
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
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if (mReviews != null) {
            if(mEmptyTextView != null) {
                if (mReviews.size() == 0) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTextView.setVisibility(View.INVISIBLE);
                }
            }
            return mReviews.size();
        }

        if(mEmptyTextView != null){
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mAuthor;
        private TextView mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_list_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_list_content);

            //TODO: make reviews expandable

        }

        public void bindView(int position){
            Review review = mReviews.get(position);

            mAuthor.setText(review.getAuthor());
            mContent.setText(review.getContent());
        }

    }
}
