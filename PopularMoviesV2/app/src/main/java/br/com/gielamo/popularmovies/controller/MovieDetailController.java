package br.com.gielamo.popularmovies.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import br.com.gielamo.popularmovies.R;
import br.com.gielamo.popularmovies.model.business.FavoriteMovieBO;
import br.com.gielamo.popularmovies.model.business.TMDbServices;
import br.com.gielamo.popularmovies.model.business.TMDbServicesFactory;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieDetail;
import br.com.gielamo.popularmovies.model.vo.MovieDetailControllerMessage;
import br.com.gielamo.popularmovies.model.vo.MovieInfo;
import br.com.gielamo.popularmovies.model.vo.MovieInfoHeader;
import br.com.gielamo.popularmovies.model.vo.Review;
import br.com.gielamo.popularmovies.model.vo.ReviewList;
import br.com.gielamo.popularmovies.model.vo.Video;
import br.com.gielamo.popularmovies.model.vo.VideoList;
import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailController extends BusController {
    private static final String LOG_TAG = MovieDetailController.class.getSimpleName();

    private static final int MOVIE_INFO_REQUEST_ID = 17;

    private static final int MORE_REVIEWS_REQUEST_ID = 18;

    private static final String MOVIE_ID_PARAM = "movieId";

    private static final String REVIEW_PAGE_PARAM = "reviewPage";

    private final AppCompatActivity mActivity;

    private final Movie mInitialMovieDetail;

    private final ArrayList<MovieInfo> mMovieInfo;

    private boolean mFavorite;

    private int mReviewPage;

    private int mTotalReviewPages;

    private boolean mLoading;

    private boolean mFavoriteChanged;

    private final Object mDataLock;

    private final LoaderManager.LoaderCallbacks<MovieDetail> mMovieInfoCallback;

    private final LoaderManager.LoaderCallbacks<ReviewList> mReviewsCallback;

    public MovieDetailController(AppCompatActivity activity, Movie movieDetail) {
        this.mActivity = activity;
        this.mInitialMovieDetail = movieDetail;
        this.mMovieInfo = new ArrayList<>();
        this.mDataLock = new Object();
        this.mMovieInfoCallback = new LoadMovieInfoCallback();
        this.mReviewsCallback = new LoadReviewsCallback();
    }

    public void loadMovieInfo() {
        synchronized (mDataLock) {
            if (!mLoading) {
                mLoading = true;

                post(MovieDetailControllerMessage.LOADING_STARTED);

                getLoaderManager().initLoader(MOVIE_INFO_REQUEST_ID,
                        getMovieRequestParams(mInitialMovieDetail.getId()), mMovieInfoCallback);
            }
        }
    }

    public void loadMoreReviews() {
        synchronized (mDataLock) {
            if (!mLoading && hasReviewsToLoad()) {
                mLoading = true;

                post(MovieDetailControllerMessage.LOADING_STARTED);

                getLoaderManager().initLoader(MORE_REVIEWS_REQUEST_ID, getReviewsRequestParams(mInitialMovieDetail.getId(), mReviewPage + 1), mReviewsCallback);
            }
        }
    }

    public List<MovieInfo> getMovieInfo() {
        return new ArrayList<>(mMovieInfo);
    }

    public boolean isLoading() {
        synchronized (mDataLock) {
            return mLoading;
        }
    }

    public boolean isFavorite() {
        synchronized (mDataLock) {
            return mFavorite;
        }
    }

    public boolean isFavoriteChanged() {
        synchronized (mDataLock) {
            return mFavoriteChanged;
        }
    }

    public boolean hasReviewsToLoad() {
        synchronized (mDataLock) {
            return mReviewPage < mTotalReviewPages;
        }
    }

    public void playVideo(Video video) {
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + video.getVideoKey());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.movie_detail_controller_video_intent_chooser_title)));
        } else {
            Toast.makeText(mActivity, R.string.movie_detail_controller_no_video_players_error_message, Toast.LENGTH_LONG).show();
        }
    }

    public void openReview(Review review) {
        Uri uri = Uri.parse(review.getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.movie_detail_controller_review_intent_chooser_title)));
        } else {
            Toast.makeText(mActivity, R.string.movie_detail_controller_no_browsers_error_message, Toast.LENGTH_LONG).show();
        }
    }

    public void switchFavorite() {
        synchronized (mDataLock) {
            if (!mLoading) {
                mFavorite = !mFavorite;
                mFavoriteChanged = !mFavoriteChanged;

                post(MovieDetailControllerMessage.FAVORITE_CHANGED);

                ChangeFavoriteStatusTask task = new ChangeFavoriteStatusTask(mInitialMovieDetail,
                        mFavorite, mActivity.getApplicationContext());

                task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
    }

    public void saveState(Bundle bundle) {
        synchronized (mDataLock) {
            bundle.putParcelableArrayList("movieInfo", mMovieInfo);
            bundle.putInt("reviewPage", mReviewPage);
            bundle.putInt("totalReviewPages", mTotalReviewPages);
            bundle.putBoolean("loading", mLoading);
            bundle.putBoolean("favorite", mFavorite);
            bundle.putBoolean("favoriteChanged", mFavoriteChanged);
        }
    }

    public void restoreState(Bundle bundle) {
        Bundle data = null;

        synchronized (mDataLock) {
            boolean loading = bundle.getBoolean("loading");
            List<MovieInfo> movieInfo = bundle.getParcelableArrayList("movieInfo");

            if ((!loading) && (movieInfo != null)) {
                mMovieInfo.addAll(movieInfo);
                mReviewPage = bundle.getInt("reviewPage");
                mTotalReviewPages = bundle.getInt("totalReviewPages");
                mFavorite = bundle.getBoolean("favorite");
                mFavoriteChanged = bundle.getBoolean("favoriteChanged");
            } else {
                data = getMovieRequestParams(mInitialMovieDetail.getId());
            }
        }

        if (data == null) {
            post(MovieDetailControllerMessage.LOADING_FINISHED);
        } else {
            getLoaderManager().initLoader(MOVIE_INFO_REQUEST_ID, data, mMovieInfoCallback);

            post(MovieDetailControllerMessage.LOADING_STARTED);
        }
    }

    private LoaderManager getLoaderManager() {
        return mActivity.getSupportLoaderManager();
    }

    private static Bundle getMovieRequestParams(long movieId) {
        Bundle bundle = new Bundle();

        bundle.putLong(MOVIE_ID_PARAM, movieId);

        return bundle;
    }

    private static Bundle getReviewsRequestParams(long movieId, int page) {
        Bundle bundle = new Bundle();

        bundle.putLong(MOVIE_ID_PARAM, movieId);
        bundle.putInt(REVIEW_PAGE_PARAM, page);

        return bundle;
    }

    private class LoadMovieInfoCallback implements LoaderManager.LoaderCallbacks<MovieDetail> {
        @Override
        public Loader<MovieDetail> onCreateLoader(int id, Bundle args) {
            return new MovieInfoLoader(mActivity, args);
        }

        @Override
        public void onLoadFinished(Loader<MovieDetail> loader, MovieDetail data) {
            boolean loaded = false;

            if (data != null) {
                synchronized (mDataLock) {
                    loaded = true;

                    mMovieInfo.clear();

                    mMovieInfo.add(data.getMovie());
                    mFavorite = data.isFavorite();

                    if ((data.getVideoList() != null) && (data.getVideoList().getVideos() != null) && (!data.getVideoList().getVideos().isEmpty())) {
                        mMovieInfo.add(new MovieInfoHeader(mActivity.getString(R.string.movie_detail_controller_trailers_section_title)));
                        mMovieInfo.addAll(data.getVideoList().getVideos());
                    }

                    if ((data.getReviewList() != null) && (data.getReviewList().getResults() != null) && (!data.getReviewList().getResults().isEmpty())) {
                        mMovieInfo.add(new MovieInfoHeader(mActivity.getString(R.string.movie_detail_controller_reviews_section_title)));
                        mMovieInfo.addAll(data.getReviewList().getResults());

                        mReviewPage = data.getReviewList().getPage();
                        mTotalReviewPages = data.getReviewList().getTotalPages();
                    } else {
                        mReviewPage = 0;
                        mTotalReviewPages = 0;
                    }

                    mLoading = false;
                }
            } else {
                synchronized (mDataLock) {
                    mLoading = false;
                }
            }

            if (loaded) {
                post(MovieDetailControllerMessage.LOADING_FINISHED);
            } else {
                post(MovieDetailControllerMessage.LOADING_ERROR);
            }
        }

        @Override
        public void onLoaderReset(Loader<MovieDetail> loader) {
            // Do nothing
        }
    }

    private class LoadReviewsCallback implements LoaderManager.LoaderCallbacks<ReviewList> {
        @Override
        public Loader<ReviewList> onCreateLoader(int id, Bundle args) {
            return new ReviewListLoader(mActivity, args);
        }

        @Override
        public void onLoadFinished(Loader<ReviewList> loader, ReviewList data) {
            boolean loaded = false;

            if (data != null) {
                synchronized (mDataLock) {
                    loaded = true;

                    mMovieInfo.addAll(data.getResults());

                    mReviewPage = data.getPage();
                    mTotalReviewPages = data.getTotalPages();

                    mLoading = false;
                }
            } else {
                synchronized (mDataLock) {
                    mReviewPage = 0;
                    mTotalReviewPages = 0;

                    mLoading = false;
                }
            }

            if (loaded) {
                post(MovieDetailControllerMessage.LOADING_FINISHED);
            } else {
                post(MovieDetailControllerMessage.LOADING_ERROR);
            }
        }

        @Override
        public void onLoaderReset(Loader<ReviewList> loader) {
            // Do nothing
        }
    }

    private static class MovieInfoLoader extends AsyncTaskLoader<MovieDetail> {
        private final Bundle mBundle;

        MovieInfoLoader(Context context, Bundle bundle) {
            super(context);

            this.mBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public MovieDetail loadInBackground() {
            MovieDetail movieDetail = null;

            TMDbServices services = TMDbServicesFactory.create();

            long movieId = mBundle.getLong(MOVIE_ID_PARAM);

            try {
                Call<Movie> movieCall = services.getMovieInfo(movieId);

                Response<Movie> movieResponse = movieCall.execute();

                if (movieResponse.isSuccessful()) {
                    movieDetail = new MovieDetail();

                    movieDetail.setMovie(movieResponse.body());
                } else {
                    Log.e(LOG_TAG, "An error has occurred while fetching the movie information for the movie id " + movieId + ".");
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not retrieve the information for movie id " + movieId + ".", e);
            }

            if (movieDetail != null) {
                FavoriteMovieBO favoriteMovieBO = new FavoriteMovieBO();
                movieDetail.setFavorite(favoriteMovieBO.isFavorite(movieId, getContext()));

                try {
                    Call<VideoList> videoListCall = services.getVideoList(movieId);

                    Response<VideoList> videoListResponse = videoListCall.execute();

                    if (videoListResponse.isSuccessful()) {
                        VideoList videoList = videoListResponse.body();

                        if (videoList != null) {
                            movieDetail.setVideoList(videoList);
                        }
                    } else {
                        Log.e(LOG_TAG, "An error has occurred while fetching the video list for the movie id " + movieId + ".");
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Could not retrieve the video list for movie id " + movieId + ".", e);
                }

                try {
                    Call<ReviewList> reviewListCall = services.getReviewList(movieId);

                    Response<ReviewList> reviewListResponse = reviewListCall.execute();

                    if (reviewListResponse.isSuccessful()) {
                        ReviewList reviewList = reviewListResponse.body();

                        if (reviewList != null) {
                            movieDetail.setReviewList(reviewList);
                        }
                    } else {
                        Log.e(LOG_TAG, "An error has occurred while fetching the review list for the movie id " + movieId + ".");
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Could not retrieve the review list for movie id " + movieId + ".", e);
                }
            }

            return movieDetail;
        }
    }

    private static class ReviewListLoader extends AsyncTaskLoader<ReviewList> {
        private final Bundle mBundle;

        ReviewListLoader(Context context, Bundle bundle) {
            super(context);

            this.mBundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ReviewList loadInBackground() {
            ReviewList reviewList = null;

            TMDbServices services = TMDbServicesFactory.create();

            long movieId = mBundle.getLong(MOVIE_ID_PARAM);
            int page = mBundle.getInt(REVIEW_PAGE_PARAM);

            try {
                Call<ReviewList> reviewListCall = services.getReviewList(movieId, page);

                Response<ReviewList> reviewListResponse = reviewListCall.execute();

                if (reviewListResponse.isSuccessful()) {
                    reviewList = reviewListResponse.body();
                } else {
                    Log.e(LOG_TAG, "An error has occurred while fetching the review list for the movie id " + movieId + ".");
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not retrieve the review list for movie id " + movieId + ".", e);
            }

            return reviewList;
        }
    }

    private static class ChangeFavoriteStatusTask extends AsyncTask<Void, Void, Void> {
        private final Movie mMovieInfo;

        private final boolean mFavorite;

        private final WeakReference<Context> mContext;

        ChangeFavoriteStatusTask(Movie movieInfo, boolean favorite, Context context) {
            this.mMovieInfo = movieInfo;
            this.mFavorite = favorite;
            this.mContext = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FavoriteMovieBO favoriteMovieBO = new FavoriteMovieBO();


            if (mFavorite) {
                favoriteMovieBO.favorite(mMovieInfo, mContext.get());
            } else {
                favoriteMovieBO.unfavorite(mMovieInfo.getId(), mContext.get());
            }

            return null;
        }
    }
}
