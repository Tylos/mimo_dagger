package com.example.tylos.daggermimo.login.api;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.model.SessionToken;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.uwetrottmann.tmdb.TmdbHelper;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;
import com.uwetrottmann.tmdb.services.MoviesService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Created by tylos on 19/3/15.
 */
public class PublicApi {

    private final RequestInterceptor publicRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
        }
    };
    
    private final AuthenticationService authenticationService = new RestAdapter.Builder()
            .setEndpoint(BuildConfig.TMDB_ROOT)
            .setRequestInterceptor(publicRequestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(AuthenticationService.class);

    private final MoviesService moviesService = new RestAdapter.Builder()
            .setEndpoint(BuildConfig.TMDB_ROOT)
            .setRequestInterceptor(publicRequestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setConverter(new GsonConverter(TmdbHelper.getGsonBuilder().create()))
            .build()
            .create(MoviesService.class);

    public SessionData login(String username, String password) {

        SessionToken session = getSessionToken(username, password);
        Account account = getAccount(session);

        return new SessionData(session, account);
    }

    private SessionToken getSessionToken(String username, String password) {
        RequestToken token;
        SessionToken session;
        try {
            token = authenticationService.generateNewRequestToken();
            token = authenticationService.validateTokenWithLogin(
                    token.getRequestToken(),
                    username,
                    password
            );
            session = authenticationService.generateSessionToken(token.getRequestToken());
        } catch (RetrofitError error) {
            session = null;
        }
        return session;
    }

    private Account getAccount(SessionToken session) {
        Account account = null;
        if (session != null) {
            try {
                account = authenticationService.getAccountDetails(session.getRequestToken());
            }catch (RetrofitError error) {
                account = null;
            }
        }
        return account;
    }

    public MovieResultsPage getUpcomingMovies() {
        return moviesService.upcoming(1, "es");
    }

}
