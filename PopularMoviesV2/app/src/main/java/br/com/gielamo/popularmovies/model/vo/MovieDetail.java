package br.com.gielamo.popularmovies.model.vo;

public class MovieDetail {
    private Movie mMovie;

    private VideoList mVideoList;

    private ReviewList mReviewList;

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
}
