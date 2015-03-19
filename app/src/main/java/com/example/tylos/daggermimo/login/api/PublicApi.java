package com.example.tylos.daggermimo.login.api;

import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionData;
import com.example.tylos.daggermimo.login.api.model.SessionToken;
import com.example.tylos.daggermimo.login.api.services.AuthenticationService;
import com.uwetrottmann.tmdb.entities.AppendToResponse;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.MovieResultsPage;
import com.uwetrottmann.tmdb.enumerations.AppendToResponseItem;
import com.uwetrottmann.tmdb.services.MoviesService;

import retrofit.RequestInterceptor;
import retrofit.RetrofitError;

/**
 * Created by tylos on 19/3/15.
 */
public class PublicApi {

    private final RequestInterceptor publicRequestInterceptor;
    private final AuthenticationService authenticationService;
    private final MoviesService moviesService;

    public PublicApi(RequestInterceptor publicRequestInterceptor, AuthenticationService authenticationService, MoviesService moviesService) {
        this.publicRequestInterceptor = publicRequestInterceptor;
        this.authenticationService = authenticationService;
        this.moviesService = moviesService;
    }

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

    public Movie getMovieDetail(int movieId) {
        return moviesService.summary(movieId, "es", new AppendToResponse(AppendToResponseItem.CREDITS));
    }

}
