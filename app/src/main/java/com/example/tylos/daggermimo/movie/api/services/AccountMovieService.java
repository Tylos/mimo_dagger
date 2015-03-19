package com.example.tylos.daggermimo.movie.api.services;

import com.example.tylos.daggermimo.login.api.model.Account;
import com.example.tylos.daggermimo.movie.api.model.AccountMovie;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tylos on 18/3/15.
 */
public interface AccountMovieService {

    @GET("/account")
    Account getAccountDetails();

    @GET("/movie/{id}/account_states")
    AccountMovie getAccountMovieData(
            @Path("id") int id
    );

    @POST("/account/{id}/favorite")
    Response postFavoriteMovie(
            @Path("id")int accountId,
            @Query("media_id") int mediaId,
            @Query("favorite")boolean addToFavorite
    );

    @POST("/account/{id}/watchlist")
    Response postWatchlistMovie(
            @Path("id")int accountId,
            @Query("media_id") int media_id,
            @Query("watchlist")boolean addToWatchlist
    );
}
