package br.com.gielamo.popularmovies.model.business;

import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieList;
import br.com.gielamo.popularmovies.model.vo.ReviewList;
import br.com.gielamo.popularmovies.model.vo.VideoList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbServices {
    @GET("movie/popular")
    Call<MovieList> getPopularMovies();

    @GET("movie/popular")
    Call<MovieList> getPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieList> getTopRatedMovies();

    @GET("movie/top_rated")
    Call<MovieList> getTopRatedMovies(@Query("page") int page);

    @GET("movie/{movieId}")
    Call<Movie> getMovieInfo(@Path("movieId") long movieId);

    @GET("movie/{movieId}/videos")
    Call<VideoList> getVideoList(@Path("movieId") long movieId);

    @GET("movie/{movieId}/reviews")
    Call<ReviewList> getReviewList(@Path("movieId") long movieId);

    @GET("movie/{movieId}/reviews")
    Call<ReviewList> getReviewList(@Path("movieId") long movieId, @Query("page") int page);
}
