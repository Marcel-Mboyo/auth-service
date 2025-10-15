package com.archer.cbs.authservice.security;

import com.archer.cbs.authservice.config.SecurityConfig;
import com.archer.cbs.authservice.config.SecurityConfig.RouteConfig;
import com.archer.cbs.authservice.dto.ApiResponse;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Optional;

/**
 * Filtre de sécurité centralisé qui utilise SecurityConfig
 * Remplace l'approche avec annotations @Secured
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class CentralizedSecurityFilter implements ContainerRequestFilter {

    @Inject
    private SecuredContext securedContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Retirer le préfixe /api si présent
        if (path.startsWith("api/")) {
            path = path.substring(4);
        }

        // Chercher la configuration de la route
        Optional<RouteConfig> routeConfig = SecurityConfig.findRoute(path, method);

        if (routeConfig.isEmpty()) {
            return; // Route non configurée, on laisse passer
        }

        RouteConfig config = routeConfig.get();
        System.out.println("=== Route trouvée: " + config.getDescription());

        // Si la route ne nécessite pas d'authentification
        if (!config.requiresAuth()) {
            return;
        }

        // Vérifier l'authentification
        if (!securedContext.isAuthenticated()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ApiResponse.error("Authentification requise pour accéder à cette ressource."))
                            .build()
            );
            return;
        }

        UserPrincipal userPrincipal = securedContext.getUserPrincipal();

        // Vérifier les rôles
        String[] requiredRoles = config.getRoles();
        if (requiredRoles.length > 0) {
            boolean hasRole = false;
            for (String role : requiredRoles) {
                if (userPrincipal.hasRole(role)) {
                    hasRole = true;
                    break;
                }
            }

            if (!hasRole) {
                requestContext.abortWith(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(ApiResponse.error(
                                        "Accès refusé. Rôles requis : " + String.join(", ", requiredRoles)
                                ))
                                .build()
                );
                return;
            }
        }

        // Vérifier les permissions
        String[] requiredPermissions = config.getPermissions();
        if (requiredPermissions.length > 0) {
            boolean hasPermission = false;
            for (String permission : requiredPermissions) {
                if (userPrincipal.hasPermission(permission)) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                requestContext.abortWith(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(ApiResponse.error(
                                        "Accès refusé. Permissions requises : " + String.join(", ", requiredPermissions)
                                ))
                                .build()
                );
                return;
            }
        }
    }
}
