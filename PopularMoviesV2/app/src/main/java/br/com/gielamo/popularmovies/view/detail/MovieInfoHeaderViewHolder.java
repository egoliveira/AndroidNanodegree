package br.com.gielamo.popularmovies.view.detail;

import android.view.View;
import android.widget.TextView;

import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.MovieInfoHeader;

public class MovieInfoHeaderViewHolder extends MovieInfoViewHolder {
    private final TextView mHeader;

    public MovieInfoHeaderViewHolder(View itemView) {
        super(itemView);

        mHeader = (TextView) itemView;
    }

    @Override
    public void bind(MovieInfo movieInfo) {
        if (movieInfo instanceof MovieInfoHeader) {
            MovieInfoHeader header = (MovieInfoHeader) movieInfo;

            mHeader.setText(header.getTitle());
        }
    }
}
