package br.com.gielamo.popularmovies.util;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import br.com.gielamo.popularmovies.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TMDbRequestInterceptor implements Interceptor {
    private static final String API_KEY_PARAM = "api_key";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        String apiKey = url.queryParameter(API_KEY_PARAM);

        if (StringUtils.isBlank(apiKey)) {
            url = url.newBuilder().addQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY).build();

            request = request.newBuilder().url(url).build();
        }

        return chain.proceed(request);
    }
}
