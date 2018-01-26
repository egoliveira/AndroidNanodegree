package br.com.gielamo.popularmovies.view.detail;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Calendar;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.ImageWidth;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;

public class MovieDetailViewHolder extends MovieInfoViewHolder {
    private final ImageView mPoster;

    private final TextView mReleaseYear;

    private final TextView mRuntimeLabel;

    private final TextView mRuntime;

    private final TextView mVoteAverage;

    private final TextView mOverview;

    public MovieDetailViewHolder(View itemView) {
        super(itemView);

        mPoster = itemView.findViewById(R.id.movie_info_view_poster);
        mReleaseYear = itemView.findViewById(R.id.movie_info_view_release_year_value);
        mRuntimeLabel = itemView.findViewById(R.id.movie_info_view_runtime_label);
        mRuntime = itemView.findViewById(R.id.movie_info_view_runtime_value);
        mVoteAverage = itemView.findViewById(R.id.movie_info_view_vote_average_value);
        mOverview = itemView.findViewById(R.id.movie_info_view_overview);
    }

    @Override
    public void bind(MovieInfo movieInfo) {
        if (movieInfo instanceof Movie) {
            Movie movie = (Movie) movieInfo;
            int width = mPoster.getWidth();
            Context context = mPoster.getContext();

            Picasso.with(context).load(movie.getPosterUrl(ImageWidth.getProperImageWidth(width))).into(mPoster);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(movie.getReleaseDate());

            mReleaseYear.setText(Integer.toString(calendar.get(Calendar.YEAR)));

            if (movie.getRuntime() != null) {
                mRuntime.setText(context.getString(R.string.movie_detail_view_holder_runtime_format,
                        movie.getRuntime()));
            } else {
                mRuntimeLabel.setVisibility(View.GONE);
                mRuntime.setVisibility(View.GONE);
            }

            NumberFormat nf = NumberFormat.getNumberInstance();
            mVoteAverage.setText(context.getString(R.string.movie_detail_view_holder_vote_average_format, nf.format(movie.getVoteAverage())));

            mOverview.setText(movie.getOverview());
        }
    }
}
