package com.archer.cbs.authservice.resource;


import com.archer.cbs.authservice.dto.ApiResponse;
import com.archer.cbs.authservice.dto.RoleDTO;
import com.archer.cbs.authservice.entity.Role;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.service.RoleService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleResource {

    @Inject
    private RoleService roleService;

    /**
     * Créer un nouveau rôle
     * POST /api/roles
     */
    @POST
    public Response createRole(@Valid RoleDTO roleDTO) {
        try {
            Role role = EntityMapper.toRole(roleDTO);
            Role created = roleService.createRole(role);
            RoleDTO result = EntityMapper.toRoleDTO(created);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Rôle créé avec succès", result))
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
     * Récupérer tous les rôles
     * GET /api/roles
     */
    @GET
    public Response getAllRoles(@QueryParam("withPermissions") boolean withPermissions) {
        try {
            List<RoleDTO> roles;

            if (withPermissions) {
                roles = roleService.getAllRolesWithPermissions()
                        .stream()
                        .map(EntityMapper::toRoleDTO)
                        .collect(Collectors.toList());
            } else {
                roles = roleService.getAllRoles()
                        .stream()
                        .map(EntityMapper::toRoleDTO)
                        .collect(Collectors.toList());
            }

            return Response.ok(ApiResponse.success("Liste des rôles", roles))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer un rôle par ID
     * GET /api/roles/{id}
     */
    @GET
    @Path("/{id}")
    public Response getRoleById(@PathParam("id") Long id,
                                @QueryParam("withPermissions") boolean withPermissions) {
        try {
            if (withPermissions) {
                return roleService.getRoleWithPermissions(id)
                        .map(role -> Response.ok(
                                ApiResponse.success("Rôle trouvé", EntityMapper.toRoleDTO(role))
                        ).build())
                        .orElse(Response.status(Response.Status.NOT_FOUND)
                                .entity(ApiResponse.error("Rôle non trouvé"))
                                .build());
            } else {
                return roleService.getRoleById(id)
                        .map(role -> Response.ok(
                                ApiResponse.success("Rôle trouvé", EntityMapper.toRoleDTO(role))
                        ).build())
                        .orElse(Response.status(Response.Status.NOT_FOUND)
                                .entity(ApiResponse.error("Rôle non trouvé"))
                                .build());
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer un rôle par nom
     * GET /api/roles/name/{nom}
     */
    @GET
    @Path("/name/{nom}")
    public Response getRoleByNom(@PathParam("nom") String nom) {
        try {
            return roleService.getRoleByNom(nom)
                    .map(role -> Response.ok(
                            ApiResponse.success("Rôle trouvé", EntityMapper.toRoleDTO(role))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Rôle non trouvé"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Mettre à jour un rôle
     * PUT /api/roles/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateRole(@PathParam("id") Long id, RoleDTO roleDTO) {
        try {
            Role role = EntityMapper.toRole(roleDTO);
            Role updated = roleService.updateRole(id, role);
            RoleDTO result = EntityMapper.toRoleDTO(updated);

            return Response.ok(ApiResponse.success("Rôle mis à jour avec succès", result))
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
     * Ajouter une permission à un rôle
     * POST /api/roles/{roleId}/permissions/{permissionId}
     */
    @POST
    @Path("/{roleId}/permissions/{permissionId}")
    public Response addPermissionToRole(@PathParam("roleId") Long roleId,
                                        @PathParam("permissionId") Long permissionId) {
        try {
            roleService.addPermissionToRole(roleId, permissionId);
            return Response.ok(ApiResponse.success("Permission ajoutée avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de l'ajout de la permission: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Retirer une permission d'un rôle
     * DELETE /api/roles/{roleId}/permissions/{permissionId}
     */
    @DELETE
    @Path("/{roleId}/permissions/{permissionId}")
    public Response removePermissionFromRole(@PathParam("roleId") Long roleId,
                                             @PathParam("permissionId") Long permissionId) {
        try {
            roleService.removePermissionFromRole(roleId, permissionId);
            return Response.ok(ApiResponse.success("Permission retirée avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du retrait de la permission: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Supprimer un rôle
     * DELETE /api/roles/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") Long id) {
        try {
            roleService.deleteRole(id);
            return Response.ok(ApiResponse.success("Rôle supprimé avec succès"))
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
     * Compter le nombre d'utilisateurs ayant un rôle
     * GET /api/roles/{id}/users/count
     */
    @GET
    @Path("/{id}/users/count")
    public Response countUsersByRole(@PathParam("id") Long id) {
        try {
            Long count = roleService.countUsersByRole(id);
            return Response.ok(ApiResponse.success("Nombre d'utilisateurs", count))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du comptage: " + e.getMessage()))
                    .build();
        }
    }
}