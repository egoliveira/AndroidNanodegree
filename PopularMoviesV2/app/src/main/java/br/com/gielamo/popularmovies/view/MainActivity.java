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
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.controller.MainController;
import br.com.gielamo.popularmovies.controller.MoviesAdapter;
import br.com.gielamo.popularmovies.model.vo.MainControllerMessage;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.view.detail.MovieDetailActivity;

public class MainActivity extends AppCompatActivity {
    private static final int MOVIE_DETAIL_REQUEST_CODE = 0x10;

    private MainController mController;

    private ViewHolder mViewHolder;

    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAdapter = new MoviesAdapter();
        mViewHolder = new ViewHolder();

        mController = new MainController(this);

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

        MenuItem item = null;

        switch (mController.getMoviesCategory()) {
            case MOST_POPULAR:
                item = menu.findItem(R.id.main_activity_menu_most_popular_movies);
                break;
            case TOP_RATED:
                item = menu.findItem(R.id.main_activity_menu_top_rated_movies);
                break;
            case FAVORITE:
                item = menu.findItem(R.id.main_activity_menu_favorite_movies);
                break;
            default:
                break;
        }

        if (item != null) {
            item.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled;

        switch (item.getItemId()) {
            case R.id.main_activity_menu_most_popular_movies:
                switchCategory(MainController.MoviesCategory.MOST_POPULAR);
                item.setChecked(true);
                handled = true;
                break;
            case R.id.main_activity_menu_top_rated_movies:
                switchCategory(MainController.MoviesCategory.TOP_RATED);
                item.setChecked(true);
                handled = true;
                break;
            case R.id.main_activity_menu_favorite_movies:
                switchCategory(MainController.MoviesCategory.FAVORITE);
                item.setChecked(true);
                handled = true;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }

        return handled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MOVIE_DETAIL_REQUEST_CODE) {
            if (resultCode == MovieDetailActivity.MOVIE_FAVORITE_STATUS_CHANGED_RESULT_CODE) {
                mController.loadFirstPage();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                List<Movie> movies = mController.getMovies();

                if (!movies.isEmpty()) {
                    mAdapter.setItems(movies);
                    mAdapter.notifyDataSetChanged();

                    mViewHolder.toNormalState();
                } else if (mController.getMoviesCategory() == MainController.MoviesCategory.FAVORITE) {
                    mViewHolder.toEmptyState();
                }

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

            startActivityForResult(intent, MOVIE_DETAIL_REQUEST_CODE);
        }
    }

    private class MoviesListScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if ((mController.getMoviesCategory() != MainController.MoviesCategory.FAVORITE) &&
                    (mViewHolder.isNMinus1RowVisible()) && (mController.canLoadMorePages())) {
                mController.loadNextPage();
            }
        }
    }

    private class ViewHolder {
        private final RecyclerView mMovies;

        private final ProgressBar mTopProgress;

        private final ProgressBar mProgress;

        private final View mErrorArea;

        private final TextView mErrorMessage;

        private final Button mTryAgainButton;

        private final GridLayoutManager mLayoutManager;

        ViewHolder() {
            mMovies = findViewById(R.id.main_activity_movies_list);
            mTopProgress = findViewById(R.id.main_activity_top_progress);
            mProgress = findViewById(R.id.main_activity_progress);
            mErrorArea = findViewById(R.id.main_activity_error_area);
            mErrorMessage = findViewById(R.id.main_activity_error_message);

            mLayoutManager = new GridLayoutManager(MainActivity.this, getMoviesPerRow(),
                    GridLayoutManager.VERTICAL, false);


            mMovies.setLayoutManager(mLayoutManager);
            mMovies.setAdapter(mAdapter);
            mMovies.addOnScrollListener(new MoviesListScrollListener());
            mMovies.setHasFixedSize(true);

            mTryAgainButton = findViewById(R.id.main_activity_try_again_button);

            mTryAgainButton.setOnClickListener(new View.OnClickListener() {
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
                mTopProgress.setVisibility(View.GONE);
                mErrorArea.setVisibility(View.GONE);
            } else {
                mTopProgress.setVisibility(View.VISIBLE);
            }
        }

        void toNormalState() {
            mMovies.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mTopProgress.setVisibility(View.GONE);
            mErrorArea.setVisibility(View.GONE);
        }

        void toErrorState() {
            int errorMessageResId;
            if (mController.getMoviesCategory() == MainController.MoviesCategory.MOST_POPULAR) {
                errorMessageResId = R.string
                        .main_activity_most_popular_movies_loading_error_message;
            } else if (mController.getMoviesCategory() == MainController.MoviesCategory.TOP_RATED) {
                errorMessageResId = R.string
                        .main_activity_top_rated_movies_loading_error_message;
            } else {
                errorMessageResId = 0;
            }

            if (mController.getMovies().isEmpty()) {
                mErrorMessage.setText(errorMessageResId);

                mMovies.setVisibility(View.INVISIBLE);
                mErrorArea.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(MainActivity.this, errorMessageResId, Toast.LENGTH_LONG).show();
            }

            mProgress.setVisibility(View.GONE);
            mTopProgress.setVisibility(View.GONE);
        }

        void toEmptyState() {
            mErrorMessage.setText(R.string.main_activity_empty_favorite_movies_list_message);

            mMovies.setVisibility(View.INVISIBLE);
            mErrorArea.setVisibility(View.VISIBLE);
            mTryAgainButton.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mTopProgress.setVisibility(View.GONE);
        }

        boolean isNMinus1RowVisible() {
            int itemPosition = mAdapter.getItemCount() / getMoviesPerRow();

            return mLayoutManager.findLastVisibleItemPosition() >= itemPosition;
        }

        int getMoviesPerRow() {
            return getResources().getInteger(R.integer.movies_list_columns);
        }
    }
}
