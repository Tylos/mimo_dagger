package com.example.tylos.daggermimo.login.api;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.model.SessionToken;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.example.tylos.daggermimo.movie.api.services.AccountMovieService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by tylos on 19/3/15.
 */
public class LoginApi {

    public SessionData login(String username, String password) {

        AuthenticationService service = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.TMDB_ROOT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(AuthenticationService.class);


        RequestToken token;
        SessionToken session;
        try {
            token = service.generateNewRequestToken();
            token = service.validateTokenWithLogin(
                    token.getRequestToken(),
                    username,
                    password
            );
            session = service.generateSessionToken(token.getRequestToken());
        } catch (RetrofitError error) {
            session = null;
        }

        Account account = null;
        final SessionToken finalSession = session;
        if (session != null) {
            try {
                AccountMovieService accountService = new RestAdapter.Builder()
                        .setEndpoint(BuildConfig.TMDB_ROOT)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addQueryParam("api_key", BuildConfig.TMDB_API_KEY);
                                request.addQueryParam("session_id", finalSession.getRequestToken());
                            }
                        })
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build()
                        .create(AccountMovieService.class);
                account = accountService.getAccountDetails();
            }catch (RetrofitError error) {
                account = null;
            }
        }

        return new SessionData(session, account);
    }

}
