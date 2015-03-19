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

    private final int accountId;
    private final AccountMovieService accountMovieService;
    private final RequestInterceptor authenticatedRequestInterceptor;

    public AuthenticatedApi(final String sessionToken, int accountId) {
        this.accountId = accountId;

        authenticatedRequestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                request.addQueryParam("session_id", sessionToken);
                request.addQueryParam("media_type", "movie");
            }
        };

        accountMovieService = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.TMDB_ROOT)
                .setRequestInterceptor(authenticatedRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
                .build()
                .create(AccountMovieService.class);;

    }

    public Response postWatchlistMovie(int movieId, boolean shouldAddToWatchlist) {
        return accountMovieService.postWatchlistMovie(accountId, movieId, shouldAddToWatchlist);
    }


    public Response postFavoriteMovie(int movieId, boolean shouldFavorite) {
        return accountMovieService.postFavoriteMovie(accountId, movieId, shouldFavorite);
    }
}
