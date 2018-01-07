package br.com.gielamo.popularmovies.util;

import android.content.Context;
import android.util.DisplayMetrics;

import br.com.gielamo.popularmovies.R;

public final class PosterUtil {
    private static final float MIN_POSTER_RATIO = 2f / 3;

    private PosterUtil() {
    }

    public static int getMoviesListPosterWidthPx(Context context) {
        int columns = context.getResources().getInteger(R.integer.movies_list_columns);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        return (int) Math.ceil((float) dm.widthPixels / columns);
    }

    public static int getMoviesListPosterMinHeightPx(Context context) {
        int posterWidth = getMoviesListPosterWidthPx(context);

        return (int) Math.ceil(posterWidth / MIN_POSTER_RATIO);
    }
}
