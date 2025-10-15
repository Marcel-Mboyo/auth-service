package com.archer.cbs.authservice.resource;

import com.archer.cbs.authservice.dto.*;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.service.UserService;
import com.archer.cbs.authservice.service.UserService.AuthenticationResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint d'authentification
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UserService userService;

    /**
     * Authentification et génération de token
     * POST /api/auth/login
     * <p>
     * Body: {
     *   "username": "admin",
     *   "password": "password123"
     * }
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            // Validation des paramètres
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Le nom d'utilisateur est requis"))
                        .build();
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Le mot de passe est requis"))
                        .build();
            }

            // Authentification
            AuthenticationResult authResult = userService.authenticate(
                    request.getUsername(),
                    request.getPassword()
            );

            if (authResult != null) {
                // Créer la réponse avec les tokens
                AuthResponse authResponse = new AuthResponse(
                        EntityMapper.toUserDTO(authResult.getUser()),
                        authResult.getAccessToken(),
                        authResult.getRefreshToken(),
                        86400 // 24 heures en secondes
                );

                return Response.ok(
                        ApiResponse.success("Authentification réussie", authResponse)
                ).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Identifiants incorrects ou compte inactif"))
                        .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de l'authentification : " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Rafraîchir le token d'accès
     * POST /api/auth/refresh
     * <p>
     * Body: {
     *   "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
     * }
     */
    @POST
    @Path("/refresh")
    public Response refreshToken(RefreshTokenRequest request) {
        try {
            if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Le refresh token est requis"))
                        .build();
            }

            AuthenticationResult authResult = userService.refreshToken(request.getRefreshToken());

            AuthResponse authResponse = new AuthResponse(
                    EntityMapper.toUserDTO(authResult.getUser()),
                    authResult.getAccessToken(),
                    authResult.getRefreshToken(),
                    86400
            );

            return Response.ok(
                    ApiResponse.success("Token rafraîchi avec succès", authResponse)
            ).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du rafraîchissement : " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Déconnexion (côté client, suppression du token)
     * POST /api/auth/logout
     * <p>
     * Note: Avec JWT stateless, la déconnexion se fait côté client
     * en supprimant le token. Pour une vraie révocation, il faudrait
     * une blacklist de tokens côté serveur.
     */
    @POST
    @Path("/logout")
    public Response logout() {
        return Response.ok(
                ApiResponse.success("Déconnexion réussie. Supprimez le token côté client.")
        ).build();
    }
}