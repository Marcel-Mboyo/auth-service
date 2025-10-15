package com.archer.cbs.authservice.service;

import com.archer.cbs.authservice.dao.RoleDAO;
import com.archer.cbs.authservice.dao.PermissionDAO;
import com.archer.cbs.authservice.entity.Role;
import com.archer.cbs.authservice.entity.Permission;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class RoleService {

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private PermissionDAO permissionDAO;

    /**
     * Créer un nouveau rôle
     */
    public Role createRole(Role role) {
        // Vérifier si le nom du rôle existe déjà
        if (roleDAO.roleExists(role.getName())) {
            throw new IllegalArgumentException("Un rôle avec ce nom existe déjà");
        }

        return roleDAO.create(role);
    }

    /**
     * Récupérer un rôle par ID
     */
    public Optional<Role> getRoleById(Long id) {
        return roleDAO.findById(id);
    }

    /**
     * Récupérer un rôle avec ses permissions
     */
    public Optional<Role> getRoleWithPermissions(Long id) {
        return roleDAO.findByIdWithPermissions(id);
    }

    /**
     * Récupérer un rôle par nom
     */
    public Optional<Role> getRoleByNom(String nom) {
        return roleDAO.findByNom(nom);
    }

    /**
     * Récupérer tous les rôles
     */
    public List<Role> getAllRoles() {
        return roleDAO.findAll();
    }

    /**
     * Récupérer tous les rôles avec leurs permissions
     */
    public List<Role> getAllRolesWithPermissions() {
        return roleDAO.findAllWithPermissions();
    }

    /**
     * Mettre à jour un rôle
     */
    public Role updateRole(Long id, Role updatedRole) {
        Role existingRole = roleDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec l'ID: " + id));

        // Vérifier si le nouveau nom existe déjà (sauf si c'est le même)
        if (!updatedRole.getName().equals(existingRole.getName()) &&
                roleDAO.roleExists(updatedRole.getName())) {
            throw new IllegalArgumentException("Un rôle avec ce nom existe déjà");
        }

        // Mise à jour des champs
        existingRole.setName(updatedRole.getName());
        existingRole.setDescription(updatedRole.getDescription());

        return roleDAO.update(existingRole);
    }

    /**
     * Ajouter une permission à un rôle
     */
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée avec l'ID: " + permissionId));

        roleDAO.addPermission(roleId, permission);
    }

    /**
     * Retirer une permission d'un rôle
     */
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée avec l'ID: " + permissionId));

        roleDAO.removePermission(roleId, permission);
    }

    /**
     * Supprimer un rôle
     */
    public boolean deleteRole(Long id) {
        if (!roleDAO.exists(id)) {
            throw new IllegalArgumentException("Rôle non trouvé avec l'ID: " + id);
        }

        // Vérifier si le rôle est utilisé par des utilisateurs
        Long userCount = roleDAO.countUsersByRole(id);
        if (userCount > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer ce rôle car il est assigné à " + userCount + " utilisateur(s)"
            );
        }

        return roleDAO.deleteById(id);
    }

    /**
     * Compter le nombre d'utilisateurs ayant un rôle
     */
    public Long countUsersByRole(Long roleId) {
        return roleDAO.countUsersByRole(roleId);
    }

    /**
     * Vérifier si un rôle existe
     */
    public boolean roleExists(String nom) {
        return roleDAO.roleExists(nom);
    }
}
