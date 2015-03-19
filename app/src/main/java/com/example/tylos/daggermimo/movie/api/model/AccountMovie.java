package com.example.tylos.daggermimo.movie.api.model;

import com.google.gson.annotations.Expose;

/**
 * Created by tylos on 18/3/15.
 */
public class AccountMovie {
    @Expose
    private long id;
    @Expose
    private boolean favorite;
    @Expose
    private boolean rated;
    @Expose
    private boolean watchlist;

    /**
     *
     * @return
     * The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The favorite
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     *
     * @param favorite
     * The favorite
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     *
     * @return
     * The rated
     */
    public boolean getRated() {
        return rated;
    }

    /**
     *
     * @param rated
     * The rated
     */
    public void setRated(boolean rated) {
        this.rated = rated;
    }

    /**
     *
     * @return
     * The watchlist
     */
    public boolean isWatchlist() {
        return watchlist;
    }

    /**
     *
     * @param watchlist
     * The watchlist
     */
    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

}
