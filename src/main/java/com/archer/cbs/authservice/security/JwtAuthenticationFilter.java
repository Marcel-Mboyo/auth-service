package com.archer.cbs.authservice.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Filtre JAX-RS qui intercepte TOUTES les requêtes
 * Pour extraire et valider le token JWT
 * <p> Middleware
 * Priority.AUTHENTICATION : S'exécute en priorité avant les autres filtres
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private JwtService jwtService;

    @Inject
    private SecuredContext securedContext;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Récupérer le header Authorization
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Si le header existe et commence par "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

            try {
                // Valider le token
                if (jwtService.validateToken(token)) {
                    // Extraire les informations de l'utilisateur
                    Long userId = jwtService.getUserIdFromToken(token);
                    String username = jwtService.getUsernameFromToken(token);
                    List<String> roles = jwtService.getRolesFromToken(token);
                    List<String> permissions = jwtService.getPermissionsFromToken(token);

                    // Créer le UserPrincipal
                    UserPrincipal userPrincipal = new UserPrincipal(userId, username, roles, permissions);

                    // Stocker dans le contexte de sécurité
                    securedContext.setUserPrincipal(userPrincipal);

                    // Créer un SecurityContext personnalisé pour JAX-RS
                    final SecurityContext originalContext = requestContext.getSecurityContext();
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return userPrincipal;
                        }

                        @Override
                        public boolean isUserInRole(String role) {
                            return userPrincipal.hasRole(role);
                        }

                        @Override
                        public boolean isSecure() {
                            return originalContext.isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return "Bearer";
                        }
                    });

                } else {
                    // Token invalide ou expiré - on ne fait rien
                    // L'annotation @Secured bloquera l'accès si nécessaire
                }
            } catch (Exception e) {
                // Erreur lors de la validation du token - on ne fait rien
                // L'annotation @Secured bloquera l'accès si nécessaire
            }
        }

        // Si pas de token ou token invalide, la requête continue
        // mais sans contexte de sécurité
        // C'est l'annotation @Secured qui décidera si on bloque ou non
    }
}