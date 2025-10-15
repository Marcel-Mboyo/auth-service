package com.archer.cbs.authservice.resource;

import com.archer.cbs.authservice.dto.ApiResponse;
import com.archer.cbs.authservice.dto.PermissionDTO;
import com.archer.cbs.authservice.entity.Permission;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.service.PermissionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermissionResource {

    @Inject
    private PermissionService permissionService;

    /**
     * Créer une nouvelle permission
     * POST /api/permissions
     */
    @POST
    public Response createPermission(PermissionDTO permissionDTO) {
        try {
            Permission permission = EntityMapper.toPermission(permissionDTO);
            Permission created = permissionService.createPermission(permission);
            PermissionDTO result = EntityMapper.toPermissionDTO(created);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Permission créée avec succès", result))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la création: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer toutes les permissions
     * GET /api/permissions
     */
    @GET
    public Response getAllPermissions() {
        try {
            List<PermissionDTO> permissions = permissionService.getAllPermissions()
                    .stream()
                    .map(EntityMapper::toPermissionDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Liste des permissions", permissions))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer une permission par ID
     * GET /api/permissions/{id}
     */
    @GET
    @Path("/{id}")
    public Response getPermissionById(@PathParam("id") Long id) {
        try {
            return permissionService.getPermissionById(id)
                    .map(permission -> Response.ok(
                            ApiResponse.success("Permission trouvée", EntityMapper.toPermissionDTO(permission))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Permission non trouvée"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer une permission par nom
     * GET /api/permissions/name/{nom}
     */
    @GET
    @Path("/name/{nom}")
    public Response getPermissionByNom(@PathParam("nom") String nom) {
        try {
            return permissionService.getPermissionByNom(nom)
                    .map(permission -> Response.ok(
                            ApiResponse.success("Permission trouvée", EntityMapper.toPermissionDTO(permission))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Permission non trouvée"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer les permissions d'un rôle
     * GET /api/permissions/role/{roleId}
     */
    @GET
    @Path("/role/{roleId}")
    public Response getPermissionsByRoleId(@PathParam("roleId") Long roleId) {
        try {
            List<PermissionDTO> permissions = permissionService.getPermissionsByRoleId(roleId)
                    .stream()
                    .map(EntityMapper::toPermissionDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Permissions du rôle", permissions))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer les permissions d'un utilisateur
     * GET /api/permissions/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getPermissionsByUserId(@PathParam("userId") Long userId) {
        try {
            List<PermissionDTO> permissions = permissionService.getPermissionsByUserId(userId)
                    .stream()
                    .map(EntityMapper::toPermissionDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Permissions de l'utilisateur", permissions))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Rechercher des permissions par nom
     * GET /api/permissions/search?nom=xxx
     */
    @GET
    @Path("/search")
    public Response searchPermissions(@QueryParam("nom") String nom) {
        try {
            List<PermissionDTO> permissions = permissionService.searchPermissionsByNom(nom)
                    .stream()
                    .map(EntityMapper::toPermissionDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Résultats de recherche", permissions))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la recherche: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Mettre à jour une permission
     * PUT /api/permissions/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updatePermission(@PathParam("id") Long id, PermissionDTO permissionDTO) {
        try {
            Permission permission = EntityMapper.toPermission(permissionDTO);
            Permission updated = permissionService.updatePermission(id, permission);
            PermissionDTO result = EntityMapper.toPermissionDTO(updated);

            return Response.ok(ApiResponse.success("Permission mise à jour avec succès", result))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la mise à jour: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Supprimer une permission
     * DELETE /api/permissions/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deletePermission(@PathParam("id") Long id) {
        try {
            permissionService.deletePermission(id);
            return Response.ok(ApiResponse.success("Permission supprimée avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la suppression: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Compter le nombre de rôles utilisant une permission
     * GET /api/permissions/{id}/roles/count
     */
    @GET
    @Path("/{id}/roles/count")
    public Response countRolesByPermission(@PathParam("id") Long id) {
        try {
            Long count = permissionService.countRolesByPermission(id);
            return Response.ok(ApiResponse.success("Nombre de rôles", count))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du comptage: " + e.getMessage()))
                    .build();
        }
    }
}
