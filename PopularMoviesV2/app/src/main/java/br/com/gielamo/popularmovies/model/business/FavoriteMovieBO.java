package br.com.gielamo.popularmovies.model.business;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;

import br.com.gielamo.popularmovies.model.persistence.PopularMoviesContract;
import br.com.gielamo.popularmovies.model.vo.Movie;
import br.com.gielamo.popularmovies.model.vo.MovieList;

public final class FavoriteMovieBO {
    private static final String LOG_TAG = FavoriteMovieBO.class.getSimpleName();

    @WorkerThread
    public boolean isFavorite(long movieId, @NonNull Context context) {
        boolean favorite = false;

        Cursor cursor = context.getContentResolver().query(
                PopularMoviesContract.MovieEntry.CONTENT_URI, null,
                PopularMoviesContract.MovieEntry.MOVIE_ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)}, null);

        if (cursor != null) {
            favorite = cursor.getCount() > 0;
            cursor.close();
        }

        return favorite;
    }

    @WorkerThread
    public void favorite(Movie movie, @NonNull Context context) {
        ContentValues values = new ContentValues();

        values.put(PopularMoviesContract.MovieEntry.MOVIE_ID_COLUMN, movie.getId());
        values.put(PopularMoviesContract.MovieEntry.VOTE_COUNT_COLUMN, movie.getVoteCount());
        values.put(PopularMoviesContract.MovieEntry.VOTE_AVERAGE_COLUMN, movie.getVoteAverage().toString());
        values.put(PopularMoviesContract.MovieEntry.TITLE_COLUMN, movie.getTitle());
        values.put(PopularMoviesContract.MovieEntry.POPULARITY_COLUMN, movie.getPopularity().toString());
        values.put(PopularMoviesContract.MovieEntry.POSTER_PATH_COLUMN, movie.getPosterPath());
        values.put(PopularMoviesContract.MovieEntry.BACKDROP_PATH_COLUMN, movie.getBackdropPath());
        values.put(PopularMoviesContract.MovieEntry.OVERVIEW_COLUMN, movie.getOverview());
        values.put(PopularMoviesContract.MovieEntry.RELEASE_DATE_COLUMN, DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(movie.getReleaseDate()));

        final ContentResolver resolver = context.getContentResolver();

        resolver.insert(PopularMoviesContract.MovieEntry.CONTENT_URI, values);
    }

    @WorkerThread
    public void unfavorite(long movieId, @NonNull Context context) {
        final ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(PopularMoviesContract.MovieEntry.CONTENT_URI,
                new String[]{PopularMoviesContract.MovieEntry._ID},
                PopularMoviesContract.MovieEntry.MOVIE_ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)}, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long movieRowId = cursor.getLong(0);

                Uri uri = ContentUris.withAppendedId(PopularMoviesContract.MovieEntry.CONTENT_URI, movieRowId);

                resolver.delete(uri, null, null);
            }

            cursor.close();
        }
    }

    @WorkerThread
    public MovieList getFavoriteMovies(@NonNull Context context) {
        MovieList movieList = new MovieList();

        movieList.setResults(new ArrayList<Movie>());

        final ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(PopularMoviesContract.MovieEntry.CONTENT_URI, null,
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Movie movie = new Movie();

                movie.setId(cursor.getLong(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.MOVIE_ID_COLUMN)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.TITLE_COLUMN)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.OVERVIEW_COLUMN)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.POSTER_PATH_COLUMN)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.BACKDROP_PATH_COLUMN)));
                movie.setPopularity(new BigDecimal(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.POPULARITY_COLUMN))));
                movie.setVoteAverage(new BigDecimal(cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.VOTE_AVERAGE_COLUMN))));
                movie.setVoteCount(cursor.getLong(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.VOTE_COUNT_COLUMN)));

                String releaseDateStr = cursor.getString(cursor.getColumnIndex(PopularMoviesContract.MovieEntry.RELEASE_DATE_COLUMN));

                try {
                    movie.setReleaseDate(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse(releaseDateStr));
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Invalid release date: " + releaseDateStr, e);
                    movie = null;
                }

                if (movie != null) {
                    movieList.getResults().add(movie);
                }
            }

            cursor.close();
        }

        return movieList;
    }
}
