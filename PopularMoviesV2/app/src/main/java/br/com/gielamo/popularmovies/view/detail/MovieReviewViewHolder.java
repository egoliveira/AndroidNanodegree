package br.com.gielamo.popularmovies.view.detail;

import android.view.View;
import android.widget.TextView;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.Review;

public class MovieReviewViewHolder extends MovieInfoViewHolder {
    private final TextView mReview;

    private final TextView mAuthor;

    private final MovieReviewClickListener mListener;

    public MovieReviewViewHolder(View itemView, MovieReviewClickListener listener) {
        super(itemView);

        mListener = listener;
        mReview = itemView.findViewById(R.id.movie_review_item_view_review);
        mAuthor = itemView.findViewById(R.id.movie_review_item_view_author);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMovieReviewClicked(getAdapterPosition());
                }
            }
        });
    }

    @Override
    public void bind(MovieInfo movieInfo) {
        if (movieInfo instanceof Review) {
            Review review = (Review) movieInfo;

            mReview.setText(review.getContent());
            mAuthor.setText(review.getAuthor());
        }
    }

    public interface MovieReviewClickListener {
        void onMovieReviewClicked(int position);
    }
}
