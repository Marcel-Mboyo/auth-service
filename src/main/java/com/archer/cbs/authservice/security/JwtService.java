package com.archer.cbs.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des tokens JWT
 * Pattern Singleton pour garantir une seule instance
 */
@Singleton
@Startup
public class JwtService {

    // Clé secrète pour signer les tokens (À EXTERNALISER EN PRODUCTION)
    private static final String SECRET_KEY = "VotreCleSecreteTresLongueEtSecuriseeQuiFaitAuMoins256Bits12345678";

    // Durée de validité du token (24 heures)
    private static final long EXPIRATION_TIME = 86400000; // 24h en millisecondes

    // Durée du refresh token (7 jours)
    private static final long REFRESH_EXPIRATION_TIME = 604800000; // 7 jours

    private Key signingKey;

    @PostConstruct
    public void init() {
        // Initialisation de la clé de signature
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur authentifié
     *
     * @param userId ID de l'utilisateur
     * @param username Nom d'utilisateur
     * @param roles Liste des rôles
     * @param permissions Liste des permissions
     * @return Token JWT signé
     */
    public String generateToken(Long userId, String username, List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("permissions", permissions);

        return createToken(claims, username, EXPIRATION_TIME);
    }

    /**
     * Génère un refresh token
     *
     * @param userId ID de l'utilisateur
     * @param username Nom d'utilisateur
     * @return Refresh token JWT
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");

        return createToken(claims, username, REFRESH_EXPIRATION_TIME);
    }

    /**
     * Crée un token JWT avec les claims spécifiés
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valide un token JWT
     *
     * @param token Token à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token invalide, expiré ou malformé
            return false;
        }
    }

    /**
     * Extrait le username du token
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait l'ID utilisateur du token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extrait les rôles du token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * Extrait les permissions du token
     */
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (List<String>) claims.get("permissions");
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Extrait une claim spécifique du token
     */
    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtient le temps restant avant expiration (en millisecondes)
     */
    public long getExpirationTime(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.getTime() - System.currentTimeMillis();
    }
}
