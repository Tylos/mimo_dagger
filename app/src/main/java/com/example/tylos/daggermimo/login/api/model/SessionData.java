package com.example.tylos.daggermimo.login.api.model;

/**
 * Created by tylos on 18/3/15.
 */
public class SessionData {

    private final SessionToken token;
    private final Account account;

    public SessionData(SessionToken token, Account account) {
        this.token = token;
        this.account = account;
    }

    public SessionToken getToken() {
        return token;
    }

    public Account getAccount() {
        return account;
    }
}
