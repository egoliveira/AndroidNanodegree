package br.com.gielamo.popularmovies.model.vo;

public class MovieDetail {
    private Movie mMovie;

    private VideoList mVideoList;

    private ReviewList mReviewList;

    private boolean mFavorite;

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
    }

    public VideoList getVideoList() {
        return mVideoList;
    }

    public void setVideoList(VideoList videoList) {
        this.mVideoList = videoList;
    }

    public ReviewList getReviewList() {
        return mReviewList;
    }

    public void setReviewList(ReviewList reviewList) {
        this.mReviewList = reviewList;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.mFavorite = favorite;
    }
}
