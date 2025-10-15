package com.archer.cbs.authservice.dto;

public class AuthResponse {
    private UserDTO user;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn; // en secondes

    // Constructeurs
    public AuthResponse() {}

    public AuthResponse(UserDTO user, String accessToken, String refreshToken, long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    // Getters et Setters
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
