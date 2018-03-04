package br.com.gielamo.popularmovies.model.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static br.com.gielamo.popularmovies.model.persistence.PopularMoviesContract.MovieEntry;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "popular_movies.db";

    private static final int DB_VERSION = 1;

    public PopularMoviesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createTableSql = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.MOVIE_ID_COLUMN + " INTEGER NOT NULL, " +
                MovieEntry.VOTE_COUNT_COLUMN + " INTEGER NOT NULL, " +
                MovieEntry.VOTE_AVERAGE_COLUMN + " REAL NOT NULL, " +
                MovieEntry.TITLE_COLUMN + " TEXT NOT NULL, " +
                MovieEntry.POPULARITY_COLUMN + " REAL NOT NULL, " +
                MovieEntry.POSTER_PATH_COLUMN + " TEXT NOT NULL, " +
                MovieEntry.BACKDROP_PATH_COLUMN + " TEXT NOT NULL, " +
                MovieEntry.OVERVIEW_COLUMN + " TEXT NOT NULL, " +
                MovieEntry.RELEASE_DATE_COLUMN + " TEXT NOT NULL);";

        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }
}
