package com.archer.cbs.authservice.dao;

import com.archer.cbs.authservice.entity.User;
import com.archer.cbs.authservice.entity.Role;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    /**
     * Trouver un utilisateur par username
     */
    public Optional<User> findByUsername(String username) {
        try {
            String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username";
            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouver un utilisateur avec ses rôles et permissions
     */
    public Optional<User> findByIdWithRolesAndPermissions(Long id) {

        try {
            String jpql = "SELECT DISTINCT u FROM User u " +
                    "LEFT JOIN FETCH u.roles r " +
                    "LEFT JOIN FETCH r.permissions " +
                    "WHERE u.id = :id";
            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            query.setParameter("id", id);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Récupérer tous les utilisateurs actifs
     */
    public List<User> findAllActive() {
        String jpql = "SELECT u FROM User u WHERE u.active = true";
        return entityManager.createQuery(jpql, User.class).getResultList();
    }

    /**
     * Récupérer tous les utilisateurs inactifs
     */
    public List<User> findAllInactive() {
        String jpql = "SELECT u FROM User u WHERE u.active = false";
        return entityManager.createQuery(jpql, User.class).getResultList();
    }

    /**
     * Trouver les utilisateurs par rôle
     */
    public List<User> findByRole(String roleName) {
        String jpql = "SELECT DISTINCT u FROM User u " +
                "JOIN u.roles r " +
                "WHERE r.name = :roleName";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("roleName", roleName);
        return query.getResultList();
    }

    /**
     * Vérifier si un username existe
     */
    public boolean usernameExists(String username) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Activer/désactiver un utilisateur
     */
    public void toggleUserStatus(Long userId) {
        Optional<User> userOpt = findById(userId);
        userOpt.ifPresent(user -> {
            user.setActive(!user.getActive());
            update(user);
        });
    }

    /**
     * Ajouter un rôle à un utilisateur
     */
    public void addRole(Long userId, Role role) {
        Optional<User> userOpt = findById(userId);
        userOpt.ifPresent(user -> {
            user.addRole(role);
            update(user);
        });
    }

    /**
     * Retirer un rôle d'un utilisateur
     */
    public void removeRole(Long userId, Role role) {
        Optional<User> userOpt = findById(userId);
        userOpt.ifPresent(user -> {
            user.removeRole(role);
            update(user);
        });
    }
}
