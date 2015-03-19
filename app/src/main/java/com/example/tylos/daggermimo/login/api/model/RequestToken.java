package com.example.tylos.daggermimo.login.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tylos on 16/3/15.
 */
public class RequestToken {

    @SerializedName("request_token")
    @Expose
    private String requestToken;
    @Expose
    private boolean success;

    /**
     *
     * @return
     * The requestToken
     */
    public String getRequestToken() {
        return requestToken;
    }

    /**
     *
     * @param requestToken
     * The request_token
     */
    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    /**
     *
     * @return
     * The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
