package br.com.gielamo.popularmovies.model.business;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import br.com.gielamo.popularmovies.model.persistence.PopularMoviesContract;
import br.com.gielamo.popularmovies.model.vo.Movie;

public final class FavoriteMovieBO {
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
        values.put(PopularMoviesContract.MovieEntry.RELEASE_DATE_COLUMN, movie.getReleaseDate().getTime());

        ContentResolver resolver = context.getContentResolver();

        resolver.insert(PopularMoviesContract.MovieEntry.CONTENT_URI, values);

        resolver.notifyChange(PopularMoviesContract.MovieEntry.CONTENT_URI, null);
    }

    @WorkerThread
    public void unfavorite(long movieId, @NonNull Context context) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(PopularMoviesContract.MovieEntry.CONTENT_URI,
                new String[]{PopularMoviesContract.MovieEntry._ID},
                PopularMoviesContract.MovieEntry.MOVIE_ID_COLUMN + "=?",
                new String[]{Long.toString(movieId)}, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long movieRowId = cursor.getLong(0);

                Uri uri = ContentUris.withAppendedId(PopularMoviesContract.MovieEntry.CONTENT_URI, movieRowId);

                resolver.delete(uri, null, null);

                resolver.notifyChange(PopularMoviesContract.MovieEntry.CONTENT_URI, null);
            }

            cursor.close();
        }
    }
}
