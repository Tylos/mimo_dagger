package com.example.tylos.daggermimo.login.api;

import com.example.tylos.daggermimo.BuildConfig;
import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.model.SessionToken;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by tylos on 19/3/15.
 */
public class PublicApi {

    public SessionData login(String username, String password) {

        SessionToken session = getSessionToken(username, password);
        Account account = getAccount(session);

        return new SessionData(session, account);
    }

    private SessionToken getSessionToken(String username, String password) {
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
        return session;
    }

    private Account getAccount(SessionToken session) {
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

        Account account = null;
        if (session != null) {
            try {
                account = service.getAccountDetails(session.getRequestToken());
            }catch (RetrofitError error) {
                account = null;
            }
        }
        return account;
    }

}
