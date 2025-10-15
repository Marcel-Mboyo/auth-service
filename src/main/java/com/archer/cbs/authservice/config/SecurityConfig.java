package com.archer.cbs.authservice.config;

import java.util.*;

/**
 * Configuration centralisée de la sécurité des routes
 * Équivalent du fichier routes de Laravel
 */
public class SecurityConfig {

    /**
     * Définition des routes et leurs règles de sécurité
     */
    public static class RouteConfig {
        private final String path;

        private final String method;

        private final boolean requiresAuth;

        private String[] roles;

        private String[] permissions;

        private String description;

        public RouteConfig(String path, String method, boolean requiresAuth) {
            this.path = path;
            this.method = method;
            this.requiresAuth = requiresAuth;
            this.roles = new String[0];
            this.permissions = new String[0];
        }

        public RouteConfig roles(String... roles) {
            this.roles = roles;
            return this;
        }

        public RouteConfig permissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public RouteConfig description(String description) {
            this.description = description;
            return this;
        }

        // Getters
        public String getPath() { return path; }
        public String getMethod() { return method; }
        public boolean requiresAuth() { return requiresAuth; }
        public String[] getRoles() { return roles; }
        public String[] getPermissions() { return permissions; }
        public String getDescription() { return description; }
    }

    /**
     * Configuration centralisée de toutes les routes
     * Comme routes/web.php ou routes/api.php dans Laravel
     */
    public static List<RouteConfig> getRoutes() {
        List<RouteConfig> routes = new ArrayList<>();

        // =============================================
        // ROUTES PUBLIQUES (sans authentification)
        // =============================================

        // Auth routes
        routes.add(new RouteConfig("/auth/login", "POST", false)
                .description("Connexion utilisateur"));

        routes.add(new RouteConfig("/auth/refresh", "POST", false)
                .description("Rafraîchir le token"));

        routes.add(new RouteConfig("/auth/register", "POST", false)
                .description("Inscription utilisateur"));

        // =============================================
        // ROUTES PROTÉGÉES (authentification requise)
        // =============================================

        // Auth routes (authentifiées)
        routes.add(new RouteConfig("/auth/logout", "POST", true)
                .description("Déconnexion"));

        routes.add(new RouteConfig("/auth/me", "GET", true)
                .description("Obtenir l'utilisateur connecté"));

        // =============================================
        // USERS - Routes authentifiées
        // =============================================

        routes.add(new RouteConfig("/users", "GET", true)
                .roles("ADMIN", "MODERATOR")
                .description("Liste des utilisateurs"));

        routes.add(new RouteConfig("/users/{id}", "GET", true)
                .description("Détails d'un utilisateur"));

        routes.add(new RouteConfig("/users", "POST", true)
                .roles("ADMIN")
                .permissions("CREATE_USER")
                .description("Créer un utilisateur"));

        routes.add(new RouteConfig("/users/with-person", "POST", true)
                .roles("ADMIN")
                .description("Créer utilisateur avec personne"));

        routes.add(new RouteConfig("/users/{id}", "PUT", true)
                .permissions("UPDATE_USER")
                .description("Modifier un utilisateur"));

        routes.add(new RouteConfig("/users/{id}", "DELETE", true)
                .roles("ADMIN")
                .permissions("DELETE_USER")
                .description("Supprimer un utilisateur"));

        routes.add(new RouteConfig("/users/{id}/change-password", "PUT", true)
                .description("Changer son mot de passe"));

        routes.add(new RouteConfig("/users/{id}/reset-password", "PUT", true)
                .roles("ADMIN")
                .description("Réinitialiser mot de passe"));

        routes.add(new RouteConfig("/users/{id}/toggle-status", "PUT", true)
                .roles("ADMIN")
                .description("Activer/désactiver utilisateur"));

        routes.add(new RouteConfig("/users/{userId}/roles/{roleId}", "POST", true)
                .roles("ADMIN")
                .description("Ajouter un rôle"));

        routes.add(new RouteConfig("/users/{userId}/roles/{roleId}", "DELETE", true)
                .roles("ADMIN")
                .description("Retirer un rôle"));

        // =============================================
        // PERSONS - Routes authentifiées
        // =============================================

        routes.add(new RouteConfig("/persons", "GET", true)
                .description("Liste des personnes"));

        routes.add(new RouteConfig("/persons/{id}", "GET", true)
                .description("Détails d'une personne"));

        routes.add(new RouteConfig("/persons/email/{email}", "GET", true)
                .description("Rechercher par email"));

        routes.add(new RouteConfig("/persons/search", "GET", true)
                .description("Rechercher des personnes"));

        routes.add(new RouteConfig("/persons", "POST", true)
                .roles("ADMIN", "MODERATOR")
                .description("Créer une personne"));

        routes.add(new RouteConfig("/persons/{id}", "PUT", true)
                .permissions("UPDATE_USER")
                .description("Modifier une personne"));

        routes.add(new RouteConfig("/persons/{id}", "DELETE", true)
                .roles("ADMIN")
                .description("Supprimer une personne"));

        routes.add(new RouteConfig("/persons/count", "GET", true)
                .description("Compter les personnes"));

        // =============================================
        // ROLES - Routes admin uniquement
        // =============================================

        routes.add(new RouteConfig("/roles", "GET", true)
                .roles("ADMIN")
                .description("Liste des rôles"));

        routes.add(new RouteConfig("/roles/{id}", "GET", true)
                .roles("ADMIN")
                .description("Détails d'un rôle"));

        routes.add(new RouteConfig("/roles/name/{nom}", "GET", true)
                .roles("ADMIN")
                .description("Rechercher un rôle par nom"));

        routes.add(new RouteConfig("/roles", "POST", true)
                .roles("ADMIN")
                .description("Créer un rôle"));

        routes.add(new RouteConfig("/roles/{id}", "PUT", true)
                .roles("ADMIN")
                .description("Modifier un rôle"));

        routes.add(new RouteConfig("/roles/{id}", "DELETE", true)
                .roles("ADMIN")
                .description("Supprimer un rôle"));

        routes.add(new RouteConfig("/roles/{roleId}/permissions/{permissionId}", "POST", true)
                .roles("ADMIN")
                .description("Ajouter permission à rôle"));

        routes.add(new RouteConfig("/roles/{roleId}/permissions/{permissionId}", "DELETE", true)
                .roles("ADMIN")
                .description("Retirer permission du rôle"));

        routes.add(new RouteConfig("/roles/{id}/users/count", "GET", true)
                .roles("ADMIN")
                .description("Compter utilisateurs par rôle"));

        // ============================================= //
        // PERMISSIONS - Routes admin uniquement         //
        // ============================================= //

        routes.add(new RouteConfig("/permissions", "GET", true)
                .roles("Super-admin")
                .description("Liste des permissions"));

        routes.add(new RouteConfig("/permissions/{id}", "GET", true)
                .roles("ADMIN")
                .description("Détails d'une permission"));

        routes.add(new RouteConfig("/permissions/name/{nom}", "GET", true)
                .roles("ADMIN")
                .description("Rechercher permission par nom"));

        routes.add(new RouteConfig("/permissions/role/{roleId}", "GET", true)
                .roles("ADMIN")
                .description("Permissions d'un rôle"));

        routes.add(new RouteConfig("/permissions/user/{userId}", "GET", true)
                .description("Permissions d'un utilisateur"));

        routes.add(new RouteConfig("/permissions/search", "GET", true)
                .roles("ADMIN")
                .description("Rechercher des permissions"));

        routes.add(new RouteConfig("/permissions", "POST", true)
                .roles("ADMIN")
                .description("Créer une permission"));

        routes.add(new RouteConfig("/permissions/{id}", "PUT", true)
                .roles("ADMIN")
                .description("Modifier une permission"));

        routes.add(new RouteConfig("/permissions/{id}", "DELETE", true)
                .roles("ADMIN")
                .description("Supprimer une permission"));

        routes.add(new RouteConfig("/permissions/{id}/roles/count", "GET", true)
                .roles("ADMIN")
                .description("Compter rôles par permission"));

        return routes;
    }

    /**
     * Trouve la configuration d'une route
     */
    public static Optional<RouteConfig> findRoute(String path, String method) {
        return getRoutes().stream()
                .filter(route -> matchesRoute(route.getPath(), path) && route.getMethod().equalsIgnoreCase(method))
                .findFirst();
    }

    /**
     * Vérifie si un path correspond à une route (avec support des paramètres)
     */
    private static boolean matchesRoute(String routePattern, String actualPath) {
        // Convertir /users/{id} en regex /users/[^/]+
        String regex = routePattern.replaceAll("\\{[^}]+\\}", "[^/]+");
        return actualPath.matches(regex);
    }

    /**
     * Obtient toutes les routes publiques
     */
    public static List<RouteConfig> getPublicRoutes() {
        return getRoutes().stream()
                .filter(route -> !route.requiresAuth())
                .toList();
    }

    /**
     * Obtient toutes les routes protégées
     */
    public static List<RouteConfig> getProtectedRoutes() {
        return getRoutes().stream()
                .filter(RouteConfig::requiresAuth)
                .toList();
    }
}
