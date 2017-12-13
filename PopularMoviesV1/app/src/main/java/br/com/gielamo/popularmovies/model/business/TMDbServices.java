package br.com.gielamo.popularmovies.model.business;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import br.com.gielamo.popularmovies.model.exception.BusinessException;
import br.com.gielamo.popularmovies.model.vo.MovieList;
import br.com.gielamo.popularmovies.util.NetworkUtil;

public class TMDbServices {
    private static final String TAG = TMDbServices.class.getSimpleName();

    private static final String ROOT_URL = "https://api.themoviedb.org/3/movie";

    public MovieList getPopularMovies(Integer page, String apiKey) throws BusinessException {
        MovieList movieList = null;

        Uri uri = getServiceUri("popular", apiKey);

        if (page != null) {
            uri = uri.buildUpon().appendQueryParameter("page", page.toString()).build();
        }

        try {
            String content = NetworkUtil.getURIContent(uri);

            if (content != null) {
                movieList = getGson().fromJson(content, MovieList.class);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not retrieve the popular movies list.", e);
            throw new BusinessException("Could not retrieve the popular movies list.", e);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Could not parse the JSON content for popular movies list.", e);
            throw new BusinessException("Could not parse the JSON content for popular movies list.", e);
        }

        return movieList;
    }

    public MovieList getTopRatedMovies(Integer page, String apiKey) throws BusinessException {
        MovieList movieList = null;

        Uri uri = getServiceUri("top_rated", apiKey);

        if (page != null) {
            uri = uri.buildUpon().appendQueryParameter("page", page.toString()).build();
        }

        try {
            String content = NetworkUtil.getURIContent(uri);

            if (content != null) {
                movieList = getGson().fromJson(content, MovieList.class);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not retrieve the top rated movies list.", e);
            throw new BusinessException("Could not retrieve the top rated movies list.", e);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Could not parse the JSON content for top rated movies list.", e);
            throw new BusinessException("Could not parse the JSON content for top rated movies list.", e);
        }

        return movieList;
    }

    private Uri getServiceUri(String servicePath, String apiKey) {
        return Uri.parse(ROOT_URL).buildUpon().appendPath(servicePath).appendQueryParameter("api_key", apiKey).build();
    }

    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        return gsonBuilder.create();
    }
}
