package br.com.gielamo.popularmovies.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.PosterWidth;
import br.com.gielamo.popularmovies.util.PosterUtil;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mPoster;

    private Listener mListener;

    public MovieViewHolder(View itemView) {
        super(itemView);

        mPoster = itemView.findViewById(R.id.movie_item_view_poster);

        int minHeight = PosterUtil.getMoviesListPosterMinHeightPx(itemView.getContext());

        itemView.setMinimumHeight(minHeight);
        mPoster.setMinimumHeight(minHeight);

        itemView.setOnClickListener(new ItemClickListener());
    }

    public void bind(final Movie movie) {
        final int posterWidthPx = PosterUtil.getMoviesListPosterWidthPx(mPoster.getContext());
        final PosterWidth posterWidth = PosterWidth.getProperPosterWidth(posterWidthPx);
        final int orientation = mPoster.getContext().getResources().getConfiguration().orientation;

        Picasso.with(mPoster.getContext()).load(movie.getPosterUrl(posterWidth)).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int height = posterWidthPx * source.getHeight() / source.getWidth();

                Bitmap newBitmap = Bitmap.createScaledBitmap(source, posterWidthPx, height, false);

                if (newBitmap != source) {
                    source.recycle();
                }

                return newBitmap;
            }

            @Override
            public String key() {
                return Long.toString(movie.getId()) + "_" + Integer.toString(orientation);
            }
        }).into(mPoster);
    }

    public void cancelPosterLoading() {
        Picasso.with(mPoster.getContext()).cancelRequest(mPoster);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public interface Listener {
        void onItemClick(int position);
    }

    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
