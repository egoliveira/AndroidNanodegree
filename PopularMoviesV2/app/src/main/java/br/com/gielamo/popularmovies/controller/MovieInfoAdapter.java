package br.com.gielamo.popularmovies.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.MovieInfoHeader;
import br.com.gielamo.popularmovies.model.vo.Review;
import br.com.gielamo.popularmovies.model.vo.Video;
import br.com.gielamo.popularmovies.view.detail.MovieDetailViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieInfoHeaderViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieInfoViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieReviewViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieVideoViewHolder;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoViewHolder> {
    private static final int MOVIE_DETAIL_TYPE = 1;

    private static final int MOVIE_VIDEO_TYPE = 2;

    private static final int MOVIE_REVIEW_TYPE = 3;

    private static final int MOVIE_INFO_SECTION_HEADER_TYPE = 4;

    private final List<MovieInfo> mInfo;

    private final MovieInfoAdapterListener mListener;

    public MovieInfoAdapter(MovieInfoAdapterListener listener) {
        mListener = listener;
        mInfo = new ArrayList<>();
    }

    @Override
    public MovieInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieInfoViewHolder vh;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MOVIE_DETAIL_TYPE) {
            View view = inflater.inflate(R.layout.movie_info_item_view, parent, false);

            vh = new MovieDetailViewHolder(view);
        } else if (viewType == MOVIE_VIDEO_TYPE) {
            View view = inflater.inflate(R.layout.movie_video_item_view, parent, false);

            vh = new MovieVideoViewHolder(view, mListener);
        } else if (viewType == MOVIE_REVIEW_TYPE) {
            View view = inflater.inflate(R.layout.movie_review_item_view, parent, false);

            vh = new MovieReviewViewHolder(view, mListener);
        } else if (viewType == MOVIE_INFO_SECTION_HEADER_TYPE) {
            View view = inflater.inflate(R.layout.movie_info_header_item_view, parent, false);

            vh = new MovieInfoHeaderViewHolder(view);
        } else {
            throw new UnsupportedOperationException("Invalid view type: " + viewType);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(MovieInfoViewHolder holder, int position) {
        MovieInfo info = mInfo.get(position);

        holder.bind(info);
    }


    @Override
    public int getItemCount() {
        return mInfo.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        MovieInfo info = mInfo.get(position);

        if (info instanceof Movie) {
            viewType = MOVIE_DETAIL_TYPE;
        } else if (info instanceof Video) {
            viewType = MOVIE_VIDEO_TYPE;
        } else if (info instanceof Review) {
            viewType = MOVIE_REVIEW_TYPE;
        } else if (info instanceof MovieInfoHeader) {
            viewType = MOVIE_INFO_SECTION_HEADER_TYPE;
        } else {
            throw new UnsupportedOperationException("position " + position + " doesn't contain a valid type.");
        }

        return viewType;
    }

    public void setItems(List<MovieInfo> info) {
        mInfo.clear();

        if (info != null) {
            mInfo.addAll(info);
        }
    }

    public interface MovieInfoAdapterListener extends MovieVideoViewHolder.MovieVideoClickListener, MovieReviewViewHolder.MovieReviewClickListener {
    }
}
