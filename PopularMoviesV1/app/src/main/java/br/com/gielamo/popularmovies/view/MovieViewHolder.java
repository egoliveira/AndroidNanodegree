package br.com.gielamo.popularmovies.view;

import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.ImageSize;
import br.com.gielamo.popularmovies.model.vo.Movie;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mPoster;

    private final TextView mTitle;

    private Listener mListener;

    public MovieViewHolder(View itemView) {
        super(itemView);

        mPoster = itemView.findViewById(R.id.movie_item_view_poster);
        mTitle = itemView.findViewById(R.id.movie_item_view_title);

        itemView.setOnClickListener(new ItemClickListener());
    }

    public void bind(Movie movie) {
        ImageSize imageSize;
        int density = mPoster.getResources().getDisplayMetrics().densityDpi;

        if (density >= DisplayMetrics.DENSITY_XXXHIGH) {
            imageSize = ImageSize.W500;
        } else if (density >= DisplayMetrics.DENSITY_XXHIGH) {
            imageSize = ImageSize.W342;
        } else if (density >= DisplayMetrics.DENSITY_XHIGH) {
            imageSize = ImageSize.W185;
        } else if (density >= DisplayMetrics.DENSITY_HIGH) {
            imageSize = ImageSize.W154;
        } else {
            imageSize = ImageSize.W92;
        }

        Picasso.with(mPoster.getContext()).load(movie.getPosterUrl(imageSize)).into(mPoster);

        mTitle.setText(movie.getTitle());
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
