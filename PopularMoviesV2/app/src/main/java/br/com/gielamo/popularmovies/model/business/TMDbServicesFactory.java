package br.com.gielamo.popularmovies.model.business;

import br.com.gielamo.popularmovies.util.TMDbRequestInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class TMDbServicesFactory {
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private TMDbServicesFactory() {
    }

    public static TMDbServices create() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new TMDbRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TMDbServices.class);
    }
}
