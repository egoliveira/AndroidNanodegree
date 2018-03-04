package br.com.gielamo.popularmovies.model.business;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.gielamo.popularmovies.model.persistence.PopularMoviesContract;
import br.com.gielamo.popularmovies.model.persistence.PopularMoviesDbHelper;

public class PopularMoviesContentProvider extends ContentProvider {
    private static final int MOVIES = 100;

    private static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private PopularMoviesDbHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new PopularMoviesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();

        int match = URI_MATCHER.match(uri);

        Cursor cursor;

        switch (match) {
            case MOVIES:
                cursor = db.query(PopularMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not Implemented.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PopularMoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);

        int affectedRows;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                affectedRows = db.delete(PopularMoviesContract.MovieEntry.TABLE_NAME, PopularMoviesContract.MovieEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = URI_MATCHER.match(uri);

        int affectedRows;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                affectedRows = db.update(PopularMoviesContract.MovieEntry.TABLE_NAME, values, PopularMoviesContract.MovieEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.MOVIES_PATH, MOVIES);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.MOVIES_PATH + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }
}
