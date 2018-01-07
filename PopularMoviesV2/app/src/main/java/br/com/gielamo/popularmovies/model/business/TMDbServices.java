package br.com.gielamo.popularmovies.model.business;

import br.com.gielamo.popularmovies.model.vo.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDbServices {
    @GET("popular")
    Call<MovieList> getPopularMovies();

    @GET("popular")
    Call<MovieList> getPopularMovies(@Query("page") int page);

    @GET("top_rated")
    Call<MovieList> getTopRatedMovies();

    @GET("top_rated")
    Call<MovieList> getTopRatedMovies(@Query("page") int page);
}
