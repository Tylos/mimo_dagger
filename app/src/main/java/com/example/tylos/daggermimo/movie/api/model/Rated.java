package com.example.tylos.daggermimo.movie.api.model;

import com.google.gson.annotations.Expose;

/**
 * Created by tylos on 18/3/15.
 */
public class Rated {

    @Expose
    private double value;

    /**
     *
     * @return
     * The value
     */
    public double getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(double value) {
        this.value = value;
    }

}
