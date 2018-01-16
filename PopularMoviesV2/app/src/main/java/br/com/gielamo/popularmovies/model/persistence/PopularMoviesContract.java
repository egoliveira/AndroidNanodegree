package br.com.gielamo.popularmovies.model.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PopularMoviesContract {
    public static final String AUTHORITY = "br.com.gielamo.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String MOVIES_PATH = "movies";

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();

        public static final String MOVIE_ID_COLUMN = "movie_id";

        public static final String VOTE_COUNT_COLUMN = "vote_count";

        public static final String VOTE_AVERAGE_COLUMN = "vote_average";

        public static final String TITLE_COLUMN = "title";

        public static final String POPULARITY_COLUMN = "popularity";

        public static final String POSTER_PATH_COLUMN = "poster_path";

        public static final String BACKDROP_PATH_COLUMN = "backdrop_path";

        public static final String OVERVIEW_COLUMN = "overview";

        public static final String RELEASE_DATE_COLUMN = "release_date";
    }
}
