package com.archer.cbs.authservice.service;

import com.archer.cbs.authservice.dao.PermissionDAO;
import com.archer.cbs.authservice.entity.Permission;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class PermissionService {

    @Inject
    private PermissionDAO permissionDAO;

    /**
     * Créer une nouvelle permission
     */
    public Permission createPermission(Permission permission) {
        // Vérifier si le nom de la permission existe déjà
        if (permissionDAO.permissionExists(permission.getName())) {
            throw new IllegalArgumentException("Une permission avec ce nom existe déjà");
        }

        return permissionDAO.create(permission);
    }

    /**
     * Récupérer une permission par ID
     */
    public Optional<Permission> getPermissionById(Long id) {
        return permissionDAO.findById(id);
    }

    /**
     * Récupérer une permission par nom
     */
    public Optional<Permission> getPermissionByNom(String nom) {
        return permissionDAO.findByNom(nom);
    }

    /**
     * Récupérer toutes les permissions
     */
    public List<Permission> getAllPermissions() {
        return permissionDAO.findAll();
    }

    /**
     * Récupérer les permissions d'un rôle
     */
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionDAO.findByRoleId(roleId);
    }

    /**
     * Récupérer les permissions d'un utilisateur
     */
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionDAO.findByUserId(userId);
    }

    /**
     * Rechercher des permissions par nom
     */
    public List<Permission> searchPermissionsByNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }
        return permissionDAO.searchByNom(nom);
    }

    /**
     * Mettre à jour une permission
     */
    public Permission updatePermission(Long id, Permission updatedPermission) {
        Permission existingPermission = permissionDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission non trouvée avec l'ID: " + id));

        // Vérifier si le nouveau nom existe déjà (sauf si c'est le même)
        if (!updatedPermission.getName().equals(existingPermission.getName()) &&
                permissionDAO.permissionExists(updatedPermission.getName())) {
            throw new IllegalArgumentException("Une permission avec ce nom existe déjà");
        }

        // Mise à jour des champs
        existingPermission.setName(updatedPermission.getName());
        existingPermission.setDescription(updatedPermission.getDescription());

        return permissionDAO.update(existingPermission);
    }

    /**
     * Supprimer une permission
     */
    public boolean deletePermission(Long id) {
        if (!permissionDAO.exists(id)) {
            throw new IllegalArgumentException("Permission non trouvée avec l'ID: " + id);
        }

        // Vérifier si la permission est utilisée par des rôles
        Long roleCount = permissionDAO.countRolesByPermission(id);
        if (roleCount > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer cette permission car elle est assignée à " + roleCount + " rôle(s)"
            );
        }

        return permissionDAO.deleteById(id);
    }

    /**
     * Compter le nombre de rôles utilisant une permission
     */
    public Long countRolesByPermission(Long permissionId) {
        return permissionDAO.countRolesByPermission(permissionId);
    }

    /**
     * Vérifier si une permission existe
     */
    public boolean permissionExists(String nom) {
        return permissionDAO.permissionExists(nom);
    }
}
