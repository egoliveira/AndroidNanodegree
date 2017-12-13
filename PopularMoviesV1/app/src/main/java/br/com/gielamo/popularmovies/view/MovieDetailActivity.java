package br.com.gielamo.popularmovies.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.vo.ImageSize;
import br.com.gielamo.popularmovies.model.vo.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String MOVIE_DATA_EXTRA = "br.com.gielamo.popularmovies.EXTRA.MOVIE_DATA";

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail_activity);

        Bundle extras = getIntent().getExtras();

        if ((extras != null) && (extras.containsKey(MOVIE_DATA_EXTRA))) {
            mMovie = extras.getParcelable(MOVIE_DATA_EXTRA);

            if (mMovie != null) {
                setTitle(mMovie.getTitle());

                ViewHolder viewHolder = new ViewHolder();

                viewHolder.update();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private class ViewHolder {
        private final ImageView mPoster;

        private final TextView mTitle;

        private final TextView mUserRating;

        private final TextView mReleaseDate;

        private final TextView mSynopsis;

        ViewHolder() {
            mPoster = findViewById(R.id.movie_detail_activity_poster);
            mTitle = findViewById(R.id.movie_detail_activity_title);
            mUserRating = findViewById(R.id.movie_detail_activity_user_rating);
            mReleaseDate = findViewById(R.id.movie_detail_activity_user_release_date);
            mSynopsis = findViewById(R.id.movie_detail_activity_synopsis);
        }

        private void update() {
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

            Picasso.with(MovieDetailActivity.this).load(mMovie.getPosterUrl(imageSize)).into
                    (mPoster);

            mTitle.setText(mMovie.getTitle());

            NumberFormat nf = NumberFormat.getNumberInstance();

            mUserRating.setText(getString(R.string.movie_detail_activity_user_rating_text, nf
                    .format(mMovie.getVoteAverage())));

            DateFormat df = DateFormat.getDateInstance();

            mReleaseDate.setText(getString(R.string.movie_detail_activity_release_date_text, df
                    .format(mMovie.getReleaseDate())));

            mSynopsis.setText(mMovie.getOverview());
        }
    }
}
