package com.archer.cbs.authservice.security;

import java.security.Principal;
import java.util.List;

/**
 * Représente l'utilisateur authentifié dans le contexte de sécurité
 */
public class UserPrincipal implements Principal {

    private final Long userId;
    private final String username;
    private final List<String> roles;
    private final List<String> permissions;

    public UserPrincipal(Long userId, String username, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public String getName() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * Vérifie si l'utilisateur a au moins un des rôles spécifiés
     */
    public boolean hasAnyRole(String... roles) {
        if (this.roles == null) return false;
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur a au moins une des permissions spécifiées
     */
    public boolean hasAnyPermission(String... permissions) {
        if (this.permissions == null) return false;
        for (String permission : permissions) {
            if (this.permissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}