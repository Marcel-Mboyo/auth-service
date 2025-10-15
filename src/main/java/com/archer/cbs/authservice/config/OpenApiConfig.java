package com.archer.cbs.authservice.config;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Configuration OpenAPI pour la documentation de l'API
 *
 * <p>Cette classe configure la génération automatique de la documentation OpenAPI
 * pour tous les endpoints REST du microservice.</p>
 *
 * <p>La documentation est accessible via :</p>
 * <ul>
 *   <li>JSON: /openapi</li>
 *   <li>YAML: /openapi?format=yaml</li>
 *   <li>Swagger UI: /openapi-ui</li>
 * </ul>
 *
 * @author Marcel Grolain
 * @version 1.0
 * @since 2024-10-15
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "1.0.0",
                description = """
            API de gestion de l'authentification, des utilisateurs et des clients.
            
            ## Fonctionnalités principales
            
            ### Authentification & Autorisation
            - Authentification JWT avec access token et refresh token
            - Gestion des rôles et permissions
            - Protection des routes avec middleware
            - Révocation de tokens (blacklist)
            
            ### Gestion des Utilisateurs
            - CRUD complet des utilisateurs
            - Gestion des rôles et permissions
            - Historique des modifications
            - Activation/désactivation de comptes
            
            ### Gestion des Clients (KYC)
            - Enregistrement des clients (particuliers et entreprises)
            - Processus KYC (Know Your Customer)
            - Gestion des documents d'identité
            - Vérification et validation
            - Historique d'audit complet
            
            ## Authentification
            
            Pour utiliser les endpoints protégés, vous devez inclure le token JWT dans le header :
```
            Authorization: Bearer <votre_token>
```
            
            Obtenez un token via l'endpoint `/auth/login`.
            
            ## Codes de statut HTTP
            
            - `200 OK` - Requête réussie
            - `201 Created` - Ressource créée avec succès
            - `400 Bad Request` - Paramètres invalides
            - `401 Unauthorized` - Authentification requise ou token invalide
            - `403 Forbidden` - Permissions insuffisantes
            - `404 Not Found` - Ressource non trouvée
            - `409 Conflict` - Conflit (ex: email déjà existant)
            - `500 Internal Server Error` - Erreur serveur
            
            ## Format des réponses
            
            Toutes les réponses suivent le format standard :
```json
            {
              "success": true|false,
              "message": "Message descriptif",
              "data": { ... }
            }
```
            """,
                contact = @Contact(
                        name = "Équipe CBS",
                        email = "support@archer-cbs.com",
                        url = "https://archer-cbs.com"
                ),
                license = @License(
                        name = "Propriétaire",
                        url = "https://archer-cbs.com/license"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080/auth-service-1.0-SNAPSHOT/api",
                        description = "Serveur de développement"
                ),
                @Server(
                        url = "https://api.archer-cbs.com",
                        description = "Serveur de production"
                )
        },
        tags = {
                @Tag(name = "Authentication", description = "Endpoints d'authentification et gestion des tokens"),
                @Tag(name = "Users", description = "Gestion des utilisateurs"),
                @Tag(name = "Persons", description = "Gestion des personnes"),
                @Tag(name = "Roles", description = "Gestion des rôles"),
                @Tag(name = "Permissions", description = "Gestion des permissions"),
                @Tag(name = "Customers", description = "Gestion des clients"),
                @Tag(name = "KYC", description = "Processus KYC et documents"),
                @Tag(name = "Routes", description = "Configuration des routes et sécurité")
        }
)
@SecurityScheme(
        securitySchemeName = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Token JWT pour l'authentification. Format: Bearer <token>"
)
public class OpenApiConfig extends Application {
    // Configuration uniquement, pas de code nécessaire
}