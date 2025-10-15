package com.archer.cbs.authservice.resource;

import com.archer.cbs.authservice.dto.*;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.security.JwtService;
import com.archer.cbs.authservice.service.UserService;
import com.archer.cbs.authservice.service.UserService.AuthenticationResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Ressource REST pour l'authentification et la gestion des tokens JWT.
 *
 * <p>Cette classe expose les endpoints pour :</p>
 * <ul>
 *   <li>Connexion utilisateur avec génération de tokens JWT</li>
 *   <li>Rafraîchissement des tokens expirés</li>
 *   <li>Déconnexion et révocation de tokens</li>
 * </ul>
 *
 * <p><strong>Routes publiques :</strong> Tous les endpoints de cette ressource
 * sont accessibles sans authentification (sauf /logout).</p>
 *
 * @author Marcel Grolain
 * @version 1.0
 * @since 2024-10-15
 * @see UserService
 * @see JwtService
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Authentification et gestion des tokens JWT")
public class AuthResource {

    @Inject
    private UserService userService;

    /**
     * Authentifie un utilisateur et génère des tokens JWT.
     *
     * <p>Cet endpoint vérifie les identifiants de l'utilisateur et, en cas de succès,
     * retourne un access token (validité 24h) et un refresh token (validité 7 jours).</p>
     *
     * <p><strong>Processus :</strong></p>
     * <ol>
     *   <li>Validation des identifiants (username/password)</li>
     *   <li>Vérification que le compte est actif</li>
     *   <li>Génération de l'access token avec rôles et permissions</li>
     *   <li>Génération du refresh token</li>
     *   <li>Retour des tokens et informations utilisateur</li>
     * </ol>
     *
     * @param request Objet contenant le username et le password
     * @return Response contenant les tokens JWT et les informations utilisateur
     *
     * @apiNote POST /api/auth/login
     *
     * @example
     * <pre>
     * {
     *   "username": "admin",
     *   "password": "password123"
     * }
     * </pre>
     */
    @POST
    @Path("/login")
    @Operation(
            summary = "Connexion utilisateur",
            description = """
            Authentifie un utilisateur et génère des tokens JWT (access token et refresh token).
            
            Le access token est valide 24 heures et doit être inclus dans toutes les requêtes protégées.
            Le refresh token est valide 7 jours et permet d'obtenir un nouveau access token.
            """
    )
    @RequestBody(
            description = "Identifiants de connexion",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Exemple de connexion",
                            value = """
                    {
                      "username": "admin",
                      "password": "password123"
                    }
                    """
                    )
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Authentification réussie",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                        {
                          "success": true,
                          "message": "Authentification réussie",
                          "data": {
                            "user": {
                              "id": 1,
                              "username": "admin",
                              "active": true,
                              "roles": ["ADMIN"]
                            },
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 86400
                          }
                        }
                        """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Paramètres invalides",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": false,
                          "message": "Le nom d'utilisateur est requis"
                        }
                        """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Identifiants incorrects ou compte inactif",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": false,
                          "message": "Identifiants incorrects ou compte inactif"
                        }
                        """
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": false,
                          "message": "Erreur lors de l'authentification : ..."
                        }
                        """
                            )
                    )
            )
    })
    public Response login(LoginRequest request) {
        try {
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

            AuthenticationResult authResult = userService.authenticate(
                    request.getUsername(),
                    request.getPassword()
            );

            if (authResult != null) {
                AuthResponse authResponse = new AuthResponse(
                        EntityMapper.toUserDTO(authResult.getUser()),
                        authResult.getAccessToken(),
                        authResult.getRefreshToken(),
                        86400
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
     * Rafraîchit un access token expiré en utilisant le refresh token.
     *
     * <p>Cet endpoint permet d'obtenir un nouveau access token sans avoir à se
     * reconnecter. Le refresh token doit être encore valide.</p>
     *
     * <p><strong>Cas d'utilisation :</strong></p>
     * <ul>
     *   <li>L'access token a expiré (401 Unauthorized)</li>
     *   <li>Le refresh token est encore valide</li>
     *   <li>L'utilisateur veut continuer sa session sans se reconnecter</li>
     * </ul>
     *
     * @param request Objet contenant le refresh token
     * @return Response contenant le nouveau access token
     *
     * @apiNote POST /api/auth/refresh
     *
     * @throws IllegalArgumentException Si le refresh token est invalide ou expiré
     */
    @POST
    @Path("/refresh")
    @Operation(
            summary = "Rafraîchir le token d'accès",
            description = """
            Génère un nouveau access token en utilisant un refresh token valide.
            Permet de prolonger la session sans redemander les identifiants.
            """
    )
    @RequestBody(
            description = "Refresh token",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RefreshTokenRequest.class),
                    examples = @ExampleObject(
                            value = """
                    {
                      "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                    }
                    """
                    )
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Token rafraîchi avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Refresh token manquant"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Refresh token invalide ou expiré"
            )
    })
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
     * Déconnecte l'utilisateur en révoquant son token.
     *
     * <p><strong>Note :</strong> Avec JWT stateless, la déconnexion se fait principalement
     * côté client en supprimant le token. Pour une vraie révocation, le token devrait
     * être ajouté à une blacklist (fonctionnalité à implémenter).</p>
     *
     * @return Response confirmant la déconnexion
     *
     * @apiNote POST /api/auth/logout
     *
     */
    @POST
    @Path("/logout")
    @Operation(
            summary = "Déconnexion",
            description = """
            Déconnecte l'utilisateur. Côté client, le token doit être supprimé.
            
            Pour une révocation complète, implémentez une blacklist de tokens.
            """
    )
    @SecurityRequirement(name = "BearerAuth")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Déconnexion réussie",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "Déconnexion réussie. Supprimez le token côté client."
                        }
                        """
                            )
                    )
            )
    })
    public Response logout() {
        return Response.ok(
                ApiResponse.success("Déconnexion réussie. Supprimez le token côté client.")
        ).build();
    }
}