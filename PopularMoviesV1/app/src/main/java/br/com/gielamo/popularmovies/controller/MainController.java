package br.com.gielamo.popularmovies.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.model.business.TMDbServices;
import br.com.gielamo.popularmovies.model.exception.BusinessException;
import br.com.gielamo.popularmovies.model.vo.MainControllerMessage;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieList;

public class MainController extends BusController {
    public enum MoviesCategory {
        MOST_POPULAR, TOP_RATED
    }

    private final List<Movie> mMovies;

    private boolean mLoading;

    private int mTotalPages;

    private int mLastLoadedPage;

    private MoviesCategory mCategory;

    private LoadMoviesTask mLoadTask;

    private final String mApiKey;

    private final Object mDataLock;

    public MainController(String apiKey) {
        mApiKey = apiKey;
        mMovies = new ArrayList<>();
        mDataLock = new Object();

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

                mLoadTask = new LoadMoviesTask(true);
                mLoadTask.execute();
            }
        }
    }

    public void loadNextPage() {
        synchronized (mDataLock) {
            if (!mLoading && canLoadMorePages()) {
                mLoading = true;

                post(MainControllerMessage.LOADING_STARTED);

                mLoadTask = new LoadMoviesTask(false);
                mLoadTask.execute();
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

    public MoviesCategory getMoviesCAtegory() {
        return mCategory;
    }

    public void reset() {
        synchronized (mDataLock) {
            if (mLoadTask != null) {
                mLoadTask.cancel(true);
                mLoadTask = null;
            }

            mLoading = false;

            mMovies.clear();
            mLastLoadedPage = 0;
            mTotalPages = 0;
            mCategory = MoviesCategory.MOST_POPULAR;
        }
    }

    public void saveState(Bundle bundle) {
        bundle.putInt("totalPages", mTotalPages);
        bundle.putInt("lastLoadedPage", mLastLoadedPage);
        bundle.putSerializable("category", mCategory);
        bundle.putParcelableArrayList("movieList", new ArrayList<>(mMovies));
    }

    public void restoreState(Bundle bundle) {
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

        post(MainControllerMessage.LOADING_FINISHED);
    }

    private class LoadMoviesTask extends AsyncTask<Void, Void, MovieList> {
        private final boolean mFirstPage;

        LoadMoviesTask(boolean firstPage) {
            this.mFirstPage = firstPage;
        }

        @Override
        protected MovieList doInBackground(Void... voids) {
            MovieList movieList = null;

            TMDbServices services = new TMDbServices();

            Integer page = null;

            if (!mFirstPage) {
                synchronized (mDataLock) {
                    page = mLastLoadedPage + 1;
                }
            }

            try {
                if (mCategory == MoviesCategory.MOST_POPULAR) {
                    movieList = services.getPopularMovies(page, mApiKey);
                } else if (mCategory == MoviesCategory.TOP_RATED) {
                    movieList = services.getTopRatedMovies(page, mApiKey);
                }
            } catch (BusinessException e) {
                // Do nothing, log already done.
            }

            return movieList;
        }

        @Override
        protected void onPostExecute(MovieList movieList) {
            if (movieList != null) {
                synchronized (mDataLock) {
                    if (mFirstPage) {
                        mMovies.clear();
                    }

                    if (movieList.getMovies() != null) {
                        mMovies.addAll(movieList.getMovies());
                        mLastLoadedPage = movieList.getPage();
                        mTotalPages = movieList.getTotalPages();
                    }

                    mLoading = false;
                }

                post(MainControllerMessage.LOADING_FINISHED);
            } else {
                synchronized (mDataLock) {
                    mLoading = false;
                }

                post(MainControllerMessage.LOADING_ERROR);
            }
        }
    }
}
