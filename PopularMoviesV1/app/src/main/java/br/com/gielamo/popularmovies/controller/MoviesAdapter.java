package br.com.gielamo.popularmovies.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.view.MovieViewHolder;

public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private final List<Movie> mMovieList;

    private Listener mListener;

    public MoviesAdapter() {
        mMovieList = new ArrayList<>();

        setHasStableIds(true);
    }

    public void setItems(List<Movie> movieList) {
        mMovieList.clear();

        if (movieList != null) {
            mMovieList.addAll(movieList);
        }
    }

    public Movie getItem(int position) {
        Movie movie = null;

        if ((position >= 0) && (position < mMovieList.size())) {
            movie = mMovieList.get(position);
        }

        return movie;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View movieView = inflater.inflate(R.layout.movie_item_view, parent, false);

        MovieViewHolder holder = new MovieViewHolder(movieView);

        holder.setListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if ((position >= 0) && (position < mMovieList.size())) {
            holder.bind(mMovieList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    @Override
    public long getItemId(int position) {
        long id = -1;

        if ((position >= 0) && (position < mMovieList.size())) {
            id = mMovieList.get(position).getId();
        }

        return id;
    }

    @Override
    public void onViewDetachedFromWindow(MovieViewHolder holder) {
        holder.cancelPosterLoading();
    }

    public interface Listener extends MovieViewHolder.Listener {
    }
}
