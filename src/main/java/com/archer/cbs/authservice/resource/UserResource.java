package com.archer.cbs.authservice.resource;


import com.archer.cbs.authservice.dto.*;
import com.archer.cbs.authservice.entity.Person;
import com.archer.cbs.authservice.entity.User;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.security.Secured;
import com.archer.cbs.authservice.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class UserResource {

    @Inject
    private UserService userService;

    /**
     * Créer un nouvel utilisateur (avec personne existante)
     * POST /api/users
     */
    @POST
    public Response createUser(@Valid UserDTO userDTO, @QueryParam("personId") Long personId) {

        try {
            User user = EntityMapper.toUser(userDTO);
            user.setPassword(userDTO.getUsername()); // Mot de passe temporaire

            User created = userService.createUser(user, personId);

            UserDTO result = EntityMapper.toUserDTO(created);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Utilisateur créé avec succès", result))
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
     * Créer un utilisateur avec une nouvelle personne
     * POST /api/users/with-person
     */
    @POST
    @Path("/with-person")
    public Response createUserWithPerson(@Valid CreateUserWithPersonRequest request) {

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());

            Person person = EntityMapper.toPerson(request.getPerson());

            User created = userService.createUserWithPerson(user, person);

            UserDTO result = EntityMapper.toUserDTO(created);


            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Utilisateur créé avec succès", result))
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
     * Récupérer tous les utilisateurs
     * GET /api/users
     */
    @GET
    @Secured(roles = {"Super-admin"})
    public Response getAllUsers(@QueryParam("active") Boolean active) {
        try {
            List<UserDTO> users;

            if (active != null) {
                users = (active ? userService.getAllActiveUsers() : userService.getAllInactiveUsers())
                        .stream()
                        .map(EntityMapper::toUserDTO)
                        .collect(Collectors.toList());
            } else {
                users = userService.getAllUsers()
                        .stream()
                        .map(EntityMapper::toUserDTO)
                        .collect(Collectors.toList());
            }

            return Response.ok(ApiResponse.success("Liste des utilisateurs", users))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer un utilisateur par ID
     * GET /api/users/{id}
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            return userService.getUserById(id)
                    .map(user -> Response.ok(
                            ApiResponse.success("Utilisateur trouvé", EntityMapper.toUserDTO(user))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Utilisateur non trouvé"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer un utilisateur avec ses rôles et permissions
     * GET /api/users/{id}/full
     */
    @GET
    @Path("/{id}/full")
    public Response getUserWithRolesAndPermissions(@PathParam("id") Long id) {
        try {
            return userService.getUserWithRolesAndPermissions(id)
                    .map(user -> Response.ok(
                            ApiResponse.success("Utilisateur trouvé", EntityMapper.toUserDTO(user))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Utilisateur non trouvé"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer un utilisateur par username
     * GET /api/users/username/{username}
     */
    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        try {
            return userService.getUserByUsername(username)
                    .map(user -> Response.ok(
                            ApiResponse.success("Utilisateur trouvé", EntityMapper.toUserDTO(user))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Utilisateur non trouvé"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer les utilisateurs par rôle
     * GET /api/users/role/{roleName}
     */
    @GET
    @Path("/role/{roleName}")
    public Response getUsersByRole(@PathParam("roleName") String roleName) {
        try {
            List<UserDTO> users = userService.getUsersByRole(roleName)
                    .stream()
                    .map(EntityMapper::toUserDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Utilisateurs trouvés", users))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Mettre à jour un utilisateur
     * PUT /api/users/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, UserDTO userDTO) {
        try {
            User user = EntityMapper.toUser(userDTO);
            User updated = userService.updateUser(id, user);
            UserDTO result = EntityMapper.toUserDTO(updated);

            return Response.ok(ApiResponse.success("Utilisateur mis à jour avec succès", result))
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
     * Changer le mot de passe
     * PUT /api/users/{id}/change-password
     */
    @PUT
    @Path("/{id}/change-password")
    public Response changePassword(@PathParam("id") Long id, ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
            return Response.ok(ApiResponse.success("Mot de passe modifié avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du changement de mot de passe: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Réinitialiser le mot de passe (admin)
     * PUT /api/users/{id}/reset-password
     */
    @PUT
    @Path("/{id}/reset-password")
    public Response resetPassword(@PathParam("id") Long id, @QueryParam("newPassword") String newPassword) {
        try {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Le nouveau mot de passe ne peut pas être vide"))
                        .build();
            }

            userService.resetPassword(id, newPassword);
            return Response.ok(ApiResponse.success("Mot de passe réinitialisé avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la réinitialisation: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Activer/Désactiver un utilisateur
     * PUT /api/users/{id}/toggle-status
     */
    @PUT
    @Path("/{id}/toggle-status")
    public Response toggleUserStatus(@PathParam("id") Long id) {
        try {
            userService.toggleUserStatus(id);
            return Response.ok(ApiResponse.success("Statut de l'utilisateur modifié avec succès"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la modification du statut: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Ajouter un rôle à un utilisateur
     * POST /api/users/{userId}/roles/{roleId}
     */
    @POST
    @Path("/{userId}/roles/{roleId}")
    public Response addRoleToUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        try {
            userService.addRoleToUser(userId, roleId);
            return Response.ok(ApiResponse.success("Rôle ajouté avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de l'ajout du rôle: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Retirer un rôle d'un utilisateur
     * DELETE /api/users/{userId}/roles/{roleId}
     */
    @DELETE
    @Path("/{userId}/roles/{roleId}")
    public Response removeRoleFromUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        try {
            userService.removeRoleFromUser(userId, roleId);
            return Response.ok(ApiResponse.success("Rôle retiré avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du retrait du rôle: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Supprimer un utilisateur
     * DELETE /api/users/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.ok(ApiResponse.success("Utilisateur supprimé avec succès"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la suppression: " + e.getMessage()))
                    .build();
        }
    }
}
