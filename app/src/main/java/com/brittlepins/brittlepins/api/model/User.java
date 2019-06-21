package com.brittlepins.brittlepins.api.model;

public class User {
    private String mID;
    private String mEmail;
    private String mUsername;
    private String mToken;
    private String csrf;

    public String getId() {
        return mID;
    }

    public void setId(String id) {
        mID = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getCSRF() {
        return this.csrf;
    }
}
