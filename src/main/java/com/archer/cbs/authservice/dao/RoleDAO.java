package com.archer.cbs.authservice.dao;

import com.archer.cbs.authservice.entity.Role;
import com.archer.cbs.authservice.entity.Permission;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RoleDAO extends BaseDAO<Role> {

    public RoleDAO() {
        super(Role.class);
    }

    /**
     * Trouver un rôle par nom
     */
    public Optional<Role> findByNom(String name) {

        try {
            String jpql = "SELECT r FROM Role r WHERE r.name = :name";
            TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
            query.setParameter("name", name);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouver un rôle avec ses permissions
     */
    public Optional<Role> findByIdWithPermissions(Long id) {
        try {
            String jpql = "SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id";
            TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Récupérer tous les rôles avec leurs permissions
     */
    public List<Role> findAllWithPermissions() {
        String jpql = "SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions";
        return entityManager.createQuery(jpql, Role.class).getResultList();
    }

    /**
     * Vérifier si un nom de rôle existe
     */
    public boolean roleExists(String name) {
        String jpql = "SELECT COUNT(r) FROM Role r WHERE r.name = :name";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Ajouter une permission à un rôle
     */
    public void addPermission(Long roleId, Permission permission) {
        Optional<Role> roleOpt = findById(roleId);
        roleOpt.ifPresent(role -> {
            role.addPermission(permission);
            update(role);
        });
    }

    /**
     * Retirer une permission d'un rôle
     */
    public void removePermission(Long roleId, Permission permission) {
        Optional<Role> roleOpt = findById(roleId);
        roleOpt.ifPresent(role -> {
            role.removePermission(permission);
            update(role);
        });
    }

    /**
     * Compter le nombre d'utilisateurs ayant un rôle
     */
    public Long countUsersByRole(Long roleId) {
        String jpql = "SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
    }
}