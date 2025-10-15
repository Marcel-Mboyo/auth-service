package com.archer.cbs.authservice.dto;

public class RefreshTokenRequest {
    private String refreshToken;

    // Constructeurs
    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getters et Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}