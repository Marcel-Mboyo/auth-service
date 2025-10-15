package com.archer.cbs.authservice.resource;

import com.archer.cbs.authservice.config.SecurityConfig;
import com.archer.cbs.authservice.config.SecurityConfig.RouteConfig;
import com.archer.cbs.authservice.dto.ApiResponse;
import com.archer.cbs.authservice.security.Secured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Endpoint pour visualiser toutes les routes
 * Équivalent de "php artisan route:list"
 */
@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutesResource {

    /**
     * Liste toutes les routes configurées
     * GET /api/routes
     */
    @GET
    @Secured(roles = {"Super-admin"})
    public Response getAllRoutes() {
        List<Map<String, Object>> routes = SecurityConfig.getRoutes().stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Liste des routes", routes))
                .build();
    }

    /**
     * Liste les routes publiques
     * GET /api/routes/public
     */
    @GET
    @Path("/public")
    public Response getPublicRoutes() {
        List<Map<String, Object>> routes = SecurityConfig.getPublicRoutes().stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Routes publiques", routes))
                .build();
    }

    /**
     * Liste les routes protégées
     * GET /api/routes/protected
     */
    @GET
    @Path("/protected")
    @Secured(roles = {"ADMIN"})
    public Response getProtectedRoutes() {
        List<Map<String, Object>> routes = SecurityConfig.getProtectedRoutes().stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Routes protégées", routes))
                .build();
    }

    /**
     * Statistiques des routes
     * GET /api/routes/stats
     */
    @GET
    @Path("/stats")
    @Secured(roles = {"ADMIN"})
    public Response getRouteStats() {
        List<RouteConfig> allRoutes = SecurityConfig.getRoutes();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allRoutes.size());
        stats.put("public", SecurityConfig.getPublicRoutes().size());
        stats.put("protected", SecurityConfig.getProtectedRoutes().size());
        stats.put("requiresRoles", allRoutes.stream()
                .filter(r -> r.getRoles().length > 0)
                .count());
        stats.put("requiresPermissions", allRoutes.stream()
                .filter(r -> r.getPermissions().length > 0)
                .count());

        // Grouper par méthode HTTP
        Map<String, Long> byMethod = allRoutes.stream()
                .collect(Collectors.groupingBy(RouteConfig::getMethod, Collectors.counting()));
        stats.put("byMethod", byMethod);

        return Response.ok(ApiResponse.success("Statistiques des routes", stats))
                .build();
    }

    /**
     * Convertit une RouteConfig en Map pour la sérialisation JSON
     */
    private Map<String, Object> toMap(RouteConfig route) {
        Map<String, Object> map = new HashMap<>();
        map.put("method", route.getMethod());
        map.put("path", "/api" + route.getPath());
        map.put("requiresAuth", route.requiresAuth());
        map.put("roles", route.getRoles());
        map.put("permissions", route.getPermissions());
        map.put("description", route.getDescription());
        return map;
    }
}
