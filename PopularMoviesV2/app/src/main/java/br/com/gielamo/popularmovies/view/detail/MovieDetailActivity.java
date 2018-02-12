package br.com.gielamo.popularmovies.view.detail;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.controller.MovieDetailController;
import br.com.gielamo.popularmovies.controller.MovieInfoAdapter;
import br.com.gielamo.popularmovies.model.vo.ImageWidth;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieDetailControllerMessage;
import br.com.gielamo.popularmovies.model.vo.Video;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String MOVIE_DATA_EXTRA = "br.com.gielamo.popularmovies.EXTRA.MOVIE_DATA";

    private MovieInfoAdapter mAdapter;

    private MovieDetailController mController;

    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_detail_activity);

        Toolbar toolbar = findViewById(R.id.movie_detail_activity_toolbar);

        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if ((extras != null) && (extras.containsKey(MOVIE_DATA_EXTRA))) {
            Movie movie = extras.getParcelable(MOVIE_DATA_EXTRA);

            if (movie != null) {
                mController = new MovieDetailController(this, movie);
                mController.register(this);

                mAdapter = new MovieInfoAdapter(new MovieInfoAdapterListener());

                mViewHolder = new ViewHolder();

                mViewHolder.setTitle(movie.getTitle());
                mViewHolder.setBackdropImage(movie);
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

    @Override
    protected void onDestroy() {
        if (mController != null) {
            mController.unregister(this);
        }

        super.onDestroy();
    }

    @Subscribe
    public void onMovieDetailControllerMessageReceived(MovieDetailControllerMessage message) {
        switch (message) {
            case LOADING_STARTED:
                mViewHolder.toLoadingState();
                break;
            case LOADING_FINISHED:
                populateAdapter();
                mViewHolder.toNormalState();
                break;
            case LOADING_ERROR:
                // TODO:
                break;
            default:
                break;
        }
    }

    private void populateAdapter() {
        mAdapter.setItems(mController.getMovieInfo());
        mAdapter.notifyDataSetChanged();
    }

    private class MovieInfoAdapterListener implements MovieInfoAdapter.MovieInfoAdapterListener {
        @Override
        public void onMovieReviewClicked(int position) {

        }

        @Override
        public void onMovieVideoClicked(int position) {
            Video video = (Video) mAdapter.getItem(position);

            mController.playVideo(video);
        }
    }

    private class ViewHolder {
        private final CollapsingToolbarLayout mToolbarLayout;

        private final ImageView mBackdropImage;

        private final RecyclerView mInfoList;

        private final ProgressBar mProgress;

        ViewHolder() {
            mToolbarLayout = findViewById(R.id.movie_detail_activity_collapsing_toolbar_layout);
            mBackdropImage = findViewById(R.id.movie_detail_activity_backdrop);
            mInfoList = findViewById(R.id.movie_detail_activity_list);
            mProgress = findViewById(R.id.movie_detail_activity_progress);

            setupUI();
        }

        private void setupUI() {
            mInfoList.setAdapter(mAdapter);
            mInfoList.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        }

        private void setTitle(CharSequence title) {
            mToolbarLayout.setTitle(title);
        }

        private void setBackdropImage(Movie movie) {
            int posterWidthPx = getResources().getDisplayMetrics().widthPixels;
            ImageWidth imageWidth = ImageWidth.getProperImageWidth(posterWidthPx);

            Picasso.with(MovieDetailActivity.this).load(movie.getBackdropUrl(imageWidth)).into(mBackdropImage);
        }

        private void toLoadingState() {
            mProgress.setVisibility(View.VISIBLE);
            mInfoList.setVisibility(View.INVISIBLE);
        }

        private void toNormalState() {
            mProgress.setVisibility(View.INVISIBLE);
            mInfoList.setVisibility(View.VISIBLE);
        }
    }
}
