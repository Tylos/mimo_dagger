package com.example.tylos.daggermimo.movie.api;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.movie.api.services.AccountMovieService;
import com.uwetrottmann.tmdb.TmdbHelper;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by tylos on 19/3/15.
 */
public class AuthenticatedApi {

    public Response postWatchlistMovie(final String sessionToken, int accountId, int movieId, boolean shouldAddToWatchlist) {
        AccountMovieService service = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.TMDB_ROOT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                        request.addQueryParam("session_id", sessionToken);
                        request.addQueryParam("media_type", "movie");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
                .build()
                .create(AccountMovieService.class);

        return service.postWatchlistMovie(accountId, movieId, shouldAddToWatchlist);
    }


    public Response postFavoriteMovie(final String sessionToken, int accountId, int movieId, boolean shouldFavorite) {
        AccountMovieService service = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.TMDB_ROOT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                        request.addQueryParam("session_id", sessionToken);
                        request.addQueryParam("media_type", "movie");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
                .build()
                .create(AccountMovieService.class);

        return service.postFavoriteMovie(accountId, movieId, shouldFavorite);
    }
}
