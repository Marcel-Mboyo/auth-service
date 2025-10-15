package com.archer.cbs.authservice.security;

import com.archer.cbs.authservice.dto.ApiResponse;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Filtre qui s'exécute sur les endpoints annotés avec @Secured
 * Vérifie que l'utilisateur a les rôles/permissions requis
 * <p>
 * Priority.AUTHORIZATION : S'exécute après l'authentification

@Provider
@Secured
// @Priority(Priorities.AUTHORIZATION)
public class SecuredFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private SecuredContext securedContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Récupérer la méthode appelée
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        // Vérifier si l'utilisateur est authentifié
        if (!securedContext.isAuthenticated()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ApiResponse.error("Authentification requise. Veuillez fournir un token valide."))
                            .build()
            );
            return;
        }

        // Récupérer l'annotation @Secured (méthode a priorité sur classe)
        Secured secured = method.getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceClass.getAnnotation(Secured.class);
        }

        if (secured != null) {
            UserPrincipal userPrincipal = securedContext.getUserPrincipal();

            // Vérifier les rôles
            String[] requiredRoles = secured.roles();
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
                                    .entity(ApiResponse.error("Accès refusé. Rôles requis : " + String.join(", ", requiredRoles)))
                                    .build()
                    );
                    return;
                }
            }

            // Vérifier les permissions
            String[] requiredPermissions = secured.permissions();
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
                                    .entity(ApiResponse.error("Accès refusé. Permissions requises : " + String.join(", ", requiredPermissions)))
                                    .build()
                    );
                    return;
                }
            }
        }

        // L'utilisateur a les autorisations nécessaires, continuer
    }
}
 */