package br.com.gielamo.popularmovies.controller;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.Video;
import br.com.gielamo.popularmovies.view.detail.MovieDetailViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieInfoViewHolder;
import br.com.gielamo.popularmovies.view.detail.MovieVideoViewHolder;

public class MovieInfoAdapter extends Adapter<MovieInfoViewHolder> {
    private static final int MOVIE_DETAIL_TYPE = 1;

    private static final int MOVIE_VIDEO_TYPE = 2;

    private final List<MovieInfo> mInfo;

    public MovieInfoAdapter() {
        mInfo = new ArrayList<>();
    }

    @Override
    public MovieInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieInfoViewHolder vh;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MOVIE_DETAIL_TYPE) {
            View view = inflater.inflate(R.layout.movie_info_view, parent, false);

            vh = new MovieDetailViewHolder(view);
        } else if (viewType == MOVIE_VIDEO_TYPE) {
            View view = inflater.inflate(R.layout.movie_video_item_view, parent, false);

            vh = new MovieVideoViewHolder(view);
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
}
