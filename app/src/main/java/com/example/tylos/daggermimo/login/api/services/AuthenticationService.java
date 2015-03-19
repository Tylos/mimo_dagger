package com.example.tylos.daggermimo.login.api.services;

import com.example.tylos.daggermimo.login.api.model.RequestToken;
import com.example.tylos.daggermimo.login.api.model.SessionToken;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Service to authenticate against TMDB
 *
 * Created by tylos on 16/3/15.
 */
public interface AuthenticationService {

    @GET("/authentication/token/new")
    RequestToken generateNewRequestToken();

    @GET("/authentication/token/validate_with_login")
    RequestToken validateTokenWithLogin(
            @Query("request_token") String requestToken,
            @Query("username") String username,
            @Query("password") String password
    );

    @GET("/authentication/session/new")
    SessionToken generateSessionToken(
            @Query("request_token") String requestToken
    );
}
