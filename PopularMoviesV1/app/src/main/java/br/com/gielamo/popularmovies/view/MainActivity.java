package br.com.gielamo.popularmovies.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.controller.MainController;
import br.com.gielamo.popularmovies.controller.MoviesAdapter;
import br.com.gielamo.popularmovies.model.vo.MainControllerMessage;

public class MainActivity extends AppCompatActivity {
    private static final int MOVIES_PER_ROW = 3;

    private MainController mController;

    private ViewHolder mViewHolder;

    private MoviesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAdapter = new MoviesAdapter();
        mViewHolder = new ViewHolder();

        mController = new MainController(getString(R.string.tmdb_api_key_v3));

        mAdapter.setListener(new MoviesAdapterListener());

        mController.register(this);

        if (savedInstanceState == null) {
            mController.loadFirstPage();
        } else {
            mController.restoreState(savedInstanceState);
        }
    }


    @Override
    protected void onDestroy() {
        mController.unregister(this);
        mController.reset();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled;

        switch (item.getItemId()) {
            case R.id.main_activity_menu_most_popular_movies:
                switchCategory(MainController.MoviesCategory.MOST_POPULAR);
                handled = true;
                break;
            case R.id.main_activity_menu_top_rated_movies:
                switchCategory(MainController.MoviesCategory.TOP_RATED);
                handled = true;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }

        return handled;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mController.saveState(outState);

        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onMainControllerMessageReceived(MainControllerMessage message) {
        switch (message) {
            case LOADING_STARTED:
                mViewHolder.toLoadingState();
                break;
            case LOADING_FINISHED:
                mAdapter.setItems(mController.getMovies());
                mAdapter.notifyDataSetChanged();

                mViewHolder.toNormalState();
                break;
            case LOADING_ERROR:
                mViewHolder.toErrorState();
                break;
            default:
                break;
        }
    }

    private void switchCategory(MainController.MoviesCategory category) {
        mAdapter.setItems(null);
        mAdapter.notifyDataSetChanged();

        mController.reset();
        mController.setMoviesCategory(category);
        mController.loadFirstPage();

        mViewHolder.mLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    private class MoviesAdapterListener implements MoviesAdapter.Listener {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

            intent.putExtra(MovieDetailActivity.MOVIE_DATA_EXTRA, mAdapter.getItem(position));

            startActivity(intent);
        }
    }

    private class MoviesListScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mViewHolder.isNMinus1RowVisible() && mController.canLoadMorePages()) {
                mController.loadNextPage();
            }
        }
    }

    private class ViewHolder {
        private final RecyclerView mMovies;

        private final ProgressBar mProgress;

        private final View mErrorArea;

        private final TextView mErrorMessage;

        private final GridLayoutManager mLayoutManager;

        ViewHolder() {
            mMovies = findViewById(R.id.main_activity_movies_list);
            mProgress = findViewById(R.id.main_activity_progress);
            mErrorArea = findViewById(R.id.main_activity_error_area);
            mErrorMessage = findViewById(R.id.main_activity_error_message);

            mLayoutManager = new GridLayoutManager(MainActivity.this, MOVIES_PER_ROW,
                    GridLayoutManager.VERTICAL, false);

            mMovies.setLayoutManager(mLayoutManager);
            mMovies.setAdapter(mAdapter);
            mMovies.addOnScrollListener(new MoviesListScrollListener());

            Button tryAgainButton = findViewById(R.id.main_activity_try_again_button);

            tryAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mController.loadFirstPage();
                }
            });
        }

        void toLoadingState() {
            if (mController.getMovies().isEmpty()) {
                mMovies.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.VISIBLE);
                mErrorArea.setVisibility(View.GONE);
            }
        }

        void toNormalState() {
            mMovies.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mErrorArea.setVisibility(View.GONE);
        }

        void toErrorState() {
            if (mController.getMoviesCAtegory() == MainController.MoviesCategory.MOST_POPULAR) {
                mErrorMessage.setText(R.string
                        .movie_detail_activity_most_popular_movies_loading_error_message);
            } else if (mController.getMoviesCAtegory() == MainController.MoviesCategory.TOP_RATED) {
                mErrorMessage.setText(R.string
                        .movie_detail_activity_top_rated_movies_loading_error_message);
            }

            mMovies.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.GONE);
            mErrorArea.setVisibility(View.VISIBLE);
        }

        boolean isNMinus1RowVisible() {
            int itemPosition = mAdapter.getItemCount() / MOVIES_PER_ROW;

            return mLayoutManager.findLastVisibleItemPosition() >= itemPosition;
        }
    }
}
