package com.archer.cbs.authservice.security;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexte de sécurité pour stocker l'utilisateur authentifié
 * Pattern RequestScoped : Une instance par requête HTTP
 */
@RequestScoped
public class SecuredContext {

    private UserPrincipal userPrincipal;

    public UserPrincipal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(UserPrincipal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    /**
     * Vérifie si un utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        return userPrincipal != null;
    }

    /**
     * Obtient l'ID de l'utilisateur courant
     */
    public Long getCurrentUserId() {
        return isAuthenticated() ? userPrincipal.getUserId() : null;
    }

    /**
     * Obtient le username de l'utilisateur courant
     */
    public String getCurrentUsername() {
        return isAuthenticated() ? userPrincipal.getUsername() : null;
    }

    /**
     * Vérifie si l'utilisateur a un rôle
     */
    public boolean hasRole(String role) {
        return isAuthenticated() && userPrincipal.hasRole(role);
    }

    /**
     * Vérifie si l'utilisateur a une permission
     */
    public boolean hasPermission(String permission) {
        return isAuthenticated() && userPrincipal.hasPermission(permission);
    }
}
