package com.humanbooster.cda.plugzy.security;

public class TokenPair {

    private String refreshToken;
    private String jwt;

    public TokenPair() {
    }

    public TokenPair(String refreshToken, String jwt) {
        this.refreshToken = refreshToken;
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
