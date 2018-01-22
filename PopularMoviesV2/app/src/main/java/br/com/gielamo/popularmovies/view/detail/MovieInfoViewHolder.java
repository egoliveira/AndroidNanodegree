package br.com.gielamo.popularmovies.view.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.gielamo.popularmovies.model.vo.MovieInfo;

public abstract class MovieInfoViewHolder extends RecyclerView.ViewHolder {
    public MovieInfoViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(MovieInfo movieInfo);
}
