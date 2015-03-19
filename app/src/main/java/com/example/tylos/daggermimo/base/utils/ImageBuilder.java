package com.example.tylos.daggermimo.base.utils;

/**
 * Created by tylos on 17/3/15.
 */
public class ImageBuilder {

    public static String generateImageUrl(String backdrop_path) {
        return "http://image.tmdb.org/t/p/w500" + backdrop_path;
    }

}
