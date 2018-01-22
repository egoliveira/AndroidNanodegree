package br.com.gielamo.popularmovies.controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.model.business.TMDbServices;
import br.com.gielamo.popularmovies.model.business.TMDbServicesFactory;
import br.com.gielamo.popularmovies.model.vo.MainControllerMessage;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieList;
import retrofit2.Call;
import retrofit2.Response;

public class MainController extends BusController {
    public enum MoviesCategory {
        MOST_POPULAR, TOP_RATED
    }

    private static final String LOG_TAG = MainController.class.getSimpleName();

    private static final int MOVIES_LIST_REQUEST_ID = 7;

    private static final String MOVIES_CATEGORY_PARAM = "category";

    private static final String PAGE_NUMBER_PARAM = "page";

    private final AppCompatActivity mActivity;

    private final List<Movie> mMovies;

    private boolean mLoading;

    private int mTotalPages;

    private int mLastLoadedPage;

    private MoviesCategory mCategory;

    private final LoaderManager.LoaderCallbacks<MovieList> mCallback;

    private final Object mDataLock;

    public MainController(AppCompatActivity activity) {
        mActivity = activity;
        mMovies = new ArrayList<>();
        mDataLock = new Object();
        mCallback = new LoadMoviesCallback();

        mCategory = MoviesCategory.MOST_POPULAR;
    }

    public List<Movie> getMovies() {
        synchronized (mDataLock) {
            return new ArrayList<>(mMovies);
        }
    }

    public void loadFirstPage() {
        synchronized (mDataLock) {
            if (!mLoading) {
                mLoading = true;

                post(MainControllerMessage.LOADING_STARTED);

                getLoaderManager().initLoader(MOVIES_LIST_REQUEST_ID,
                        getRequestParams(mCategory, null),
                        mCallback);
            }
        }
    }

    public void loadNextPage() {
        synchronized (mDataLock) {
            if (!mLoading && canLoadMorePages()) {
                mLoading = true;

                post(MainControllerMessage.LOADING_STARTED);

                getLoaderManager().restartLoader(MOVIES_LIST_REQUEST_ID,
                        getRequestParams(mCategory, mLastLoadedPage + 1),
                        mCallback);
            }
        }
    }

    public boolean canLoadMorePages() {
        synchronized (mDataLock) {
            return (mTotalPages == 0) || (mLastLoadedPage < mTotalPages);
        }
    }

    public void setMoviesCategory(MoviesCategory category) {
        if (category == null) {
            mCategory = MoviesCategory.MOST_POPULAR;
        } else {
            mCategory = category;
        }
    }

    public MoviesCategory getMoviesCategory() {
        return mCategory;
    }

    public void reset() {
        synchronized (mDataLock) {
            getLoaderManager().destroyLoader(MOVIES_LIST_REQUEST_ID);

            mLoading = false;

            mMovies.clear();
            mLastLoadedPage = 0;
            mTotalPages = 0;
            mCategory = MoviesCategory.MOST_POPULAR;
        }
    }

    public void saveState(Bundle bundle) {
        synchronized (mDataLock) {
            bundle.putInt("totalPages", mTotalPages);
            bundle.putInt("lastLoadedPage", mLastLoadedPage);
            bundle.putSerializable("category", mCategory);
            bundle.putParcelableArrayList("movieList", new ArrayList<>(mMovies));
            bundle.putBoolean("loading", mLoading);
        }
    }

    public void restoreState(Bundle bundle) {
        Bundle data = null;

        synchronized (mDataLock) {
            mTotalPages = bundle.getInt("totalPages");
            mLastLoadedPage = bundle.getInt("lastLoadedPage");
            mCategory = (MoviesCategory) bundle.getSerializable("category");

            mMovies.clear();

            List<Parcelable> movies = bundle.getParcelableArrayList("movieList");

            if (movies != null) {
                for (Parcelable p : movies) {
                    if (p instanceof Movie) {
                        mMovies.add((Movie) p);
                    }
                }
            }

            mLoading = bundle.getBoolean("loading");

            if (mLoading) {
                Integer page = null;

                if (mLastLoadedPage > 0) {
                    page = mLastLoadedPage + 1;
                }

                data = getRequestParams(mCategory, page);
            }
        }

        if (data == null) {
            post(MainControllerMessage.LOADING_FINISHED);
        } else {
            getLoaderManager().restartLoader(MOVIES_LIST_REQUEST_ID, data, mCallback);

            post(MainControllerMessage.LOADING_STARTED);
        }
    }

    private LoaderManager getLoaderManager() {
        return mActivity.getSupportLoaderManager();
    }

    private static Bundle getRequestParams(MoviesCategory category, Integer pageNumber) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(MOVIES_CATEGORY_PARAM, category);

        if (pageNumber != null) {
            bundle.putInt(PAGE_NUMBER_PARAM, pageNumber);
        }

        return bundle;
    }

    private class LoadMoviesCallback implements LoaderManager.LoaderCallbacks<MovieList> {
        @Override
        public Loader<MovieList> onCreateLoader(int id, Bundle args) {
            return new MoviesListLoader(mActivity, args);
        }

        @Override
        public void onLoadFinished(Loader<MovieList> loader, MovieList data) {
            boolean loaded = false;

            if (data != null) {
                synchronized (mDataLock) {
                    if (data.getResults() != null) {
                        loaded = true;

                        mLastLoadedPage = data.getPage();

                        if (mLastLoadedPage == 1) {
                            mMovies.clear();
                        }

                        mMovies.addAll(data.getResults());
                        mTotalPages = data.getTotalPages();
                    }

                    mLoading = false;
                }
            } else {
                synchronized (mDataLock) {
                    mLoading = false;
                }
            }

            if (loaded) {
                post(MainControllerMessage.LOADING_FINISHED);
            } else {
                post(MainControllerMessage.LOADING_ERROR);
            }
        }

        @Override
        public void onLoaderReset(Loader<MovieList> loader) {
            // Do nothing
        }
    }

    private static class MoviesListLoader extends AsyncTaskLoader<MovieList> {
        private final Bundle mBundle;

        MoviesListLoader(Context context, Bundle bundle) {
            super(context);

            this.mBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public MovieList loadInBackground() {
            MovieList movieList = null;

            TMDbServices services = TMDbServicesFactory.create();

            MoviesCategory category = (MoviesCategory) mBundle.getSerializable(MOVIES_CATEGORY_PARAM);
            int page = mBundle.getInt(PAGE_NUMBER_PARAM, -1);

            try {
                Call<MovieList> call = null;

                if (category == MoviesCategory.MOST_POPULAR) {
                    if (page == -1) {
                        call = services.getPopularMovies();
                    } else {
                        call = services.getPopularMovies(page);
                    }
                } else if (category == MoviesCategory.TOP_RATED) {
                    if (page == -1) {
                        call = services.getTopRatedMovies();
                    } else {
                        call = services.getTopRatedMovies(page);
                    }
                } else {
                    Log.e(LOG_TAG, "Invalid category: " + category);
                }

                if (call != null) {
                    Response<MovieList> response = call.execute();

                    if (response.isSuccessful()) {
                        movieList = response.body();
                    } else {
                        Log.e(LOG_TAG, "An error has occurred while fetching the movies list for the category " + category);
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not retrieve the " + category + " movies list.", e);
            }

            return movieList;
        }
    }
}
