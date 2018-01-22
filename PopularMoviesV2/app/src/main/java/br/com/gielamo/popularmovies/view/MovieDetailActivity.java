package br.com.gielamo.popularmovies.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.controller.MovieDetailController;
import br.com.gielamo.popularmovies.controller.MovieInfoAdapter;
import br.com.gielamo.popularmovies.model.vo.ImageWidth;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String MOVIE_DATA_EXTRA = "br.com.gielamo.popularmovies.EXTRA.MOVIE_DATA";

    private Movie mMovie;

    private MovieInfoAdapter mAdapter;

    private MovieDetailController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail_activity);

        Bundle extras = getIntent().getExtras();

        if ((extras != null) && (extras.containsKey(MOVIE_DATA_EXTRA))) {
            mMovie = extras.getParcelable(MOVIE_DATA_EXTRA);

            if (mMovie != null) {
                mController = new MovieDetailController(this, mMovie);
                
                mAdapter = new MovieInfoAdapter();
                populateAdapter();

                setTitle(mMovie.getTitle());

                ImageView im = findViewById(R.id.movie_detail_activity_backdrop);

                int posterWidthPx = getResources().getDisplayMetrics().widthPixels;
                ImageWidth imageWidth = ImageWidth.getProperImageWidth(posterWidthPx);

                Picasso.with(this).load(mMovie.getBackdropUrl(imageWidth)).into(im);
                ViewHolder viewHolder = new ViewHolder();
            } else {
                finish();
            }
        } else {
            finish();
        }

        if (mController != null) {
            if (savedInstanceState == null) {
                mController.loadMovieInfo();
            } else {
                mController.restoreState(savedInstanceState);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mController.saveState(outState);
    }

    private void populateAdapter() {
        List<MovieInfo> info = new ArrayList<>();

        info.add(mMovie);

        mAdapter.setItems(info);
        mAdapter.notifyDataSetChanged();
    }

    private class ViewHolder {
        private final RecyclerView mInfoList;

        ViewHolder() {
            mInfoList = findViewById(R.id.movie_detail_activity_list);

            setupUI();
        }

        private void setupUI() {
            mInfoList.setAdapter(mAdapter);
            mInfoList.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        }
    }
}
