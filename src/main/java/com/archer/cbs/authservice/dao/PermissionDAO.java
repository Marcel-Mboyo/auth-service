package com.archer.cbs.authservice.dao;

import com.archer.cbs.authservice.entity.Permission;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class PermissionDAO extends BaseDAO<Permission> {

    public PermissionDAO() {
        super(Permission.class);
    }

    /**
     * Trouver une permission par nom
     */
    public Optional<Permission> findByNom(String name) {

        try {
            String jpql = "SELECT p FROM Permission p WHERE p.name = :name";
            TypedQuery<Permission> query = entityManager.createQuery(jpql, Permission.class);
            query.setParameter("name", name);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouver les permissions d'un rôle
     */
    public List<Permission> findByRoleId(Long roleId) {
        String jpql = "SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId";
        TypedQuery<Permission> query = entityManager.createQuery(jpql, Permission.class);
        query.setParameter("roleId", roleId);
        return query.getResultList();
    }

    /**
     * Trouver les permissions d'un utilisateur (via ses rôles)
     */
    public List<Permission> findByUserId(Long userId) {
        String jpql = "SELECT DISTINCT p FROM Permission p " +
                "JOIN p.roles r " +
                "JOIN r.users u " +
                "WHERE u.id = :userId";
        TypedQuery<Permission> query = entityManager.createQuery(jpql, Permission.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    /**
     * Vérifier si un nom de permission existe
     */
    public boolean permissionExists(String name) {
        String jpql = "SELECT COUNT(p) FROM Permission p WHERE p.name = :name";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Compter le nombre de rôles utilisant une permission
     */
    public Long countRolesByPermission(Long permissionId) {
        String jpql = "SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("permissionId", permissionId)
                .getSingleResult();
    }

    /**
     * Rechercher des permissions par nom (partiel)
     */
    public List<Permission> searchByNom(String name) {
        String jpql = "SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(:name)";
        TypedQuery<Permission> query = entityManager.createQuery(jpql, Permission.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
}