package com.archer.cbs.authservice.service;

import com.archer.cbs.authservice.dao.PermissionDAO;
import com.archer.cbs.authservice.dao.UserDAO;
import com.archer.cbs.authservice.dao.RoleDAO;
import com.archer.cbs.authservice.dao.PersonDAO;
import com.archer.cbs.authservice.entity.Permission;
import com.archer.cbs.authservice.entity.User;
import com.archer.cbs.authservice.entity.Role;
import com.archer.cbs.authservice.entity.Person;
import com.archer.cbs.authservice.security.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@Transactional
public class UserService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private PermissionDAO permissionDAO;

    @Inject
    private JwtService jwtService;

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private PersonDAO personDAO;

    /**
     * Créer un nouvel utilisateur
     */
    public User createUser(User user, Long personId) {
        // Vérifier si le username existe déjà
        if (userDAO.usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        // Associer la personne si l'ID est fourni
        if (personId != null) {
            Person person = personDAO.findById(personId)
                    .orElseThrow(() -> new IllegalArgumentException("Personne non trouvée avec l'ID: " + personId));
            user.setPerson(person);
        }

        // Hasher le mot de passe
        user.setPassword(hashPassword(user.getPassword()));

        return userDAO.create(user);
    }

    /**
     * Créer un utilisateur avec une nouvelle personne
     */
    public User createUserWithPerson(User user, Person person) throws JsonProcessingException {
        // Vérifier si le username existe déjà
        if (userDAO.usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        // Vérifier si l'email existe déjà
        if (person.getEmail() != null && personDAO.emailExists(person.getEmail())) {
            throw new IllegalArgumentException("Une personne avec cet email existe déjà");
        }

        // Créer d'abord la personne
        Person savedPerson = personDAO.create(person);

        // Associer la personne à l'utilisateur
        user.setPerson(savedPerson);

        // Hasher le mot de passe
        user.setPassword(hashPassword(user.getPassword()));

        return userDAO.create(user);
    }

    /**
     * Authentifier un utilisateur et générer un token JWT
     *
     * @return Map contenant l'utilisateur et les tokens
     */
    public AuthenticationResult authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = hashPassword(password);

            if (user.getPassword().equals(hashedPassword) && user.getActive()) {

                // Extraire les rôles
                List<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                // Extraire les permissions (via les rôles)
                List<String> permissions = permissionDAO.findByUserId(user.getId())
                        .stream()
                        .map(Permission::getName)
                        .distinct()
                        .collect(Collectors.toList());

                // Générer les tokens
                String accessToken = jwtService.generateToken(
                        user.getId(),
                        user.getUsername(),
                        roles,
                        permissions
                );

                String refreshToken = jwtService.generateRefreshToken(
                        user.getId(),
                        user.getUsername()
                );

                return new AuthenticationResult(user, accessToken, refreshToken);
            }
        }

        return null; // Authentification échouée
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public Optional<User> getUserById(Long id) {
        return userDAO.findById(id);
    }

    /**
     * Récupérer un utilisateur avec ses rôles et permissions
     */
    public Optional<User> getUserWithRolesAndPermissions(Long id) {
        return userDAO.findByIdWithRolesAndPermissions(id);
    }

    /**
     * Récupérer un utilisateur par username
     */
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Récupérer tous les utilisateurs actifs
     */
    public List<User> getAllActiveUsers() {
        return userDAO.findAllActive();
    }

    /**
     * Récupérer tous les utilisateurs inactifs
     */
    public List<User> getAllInactiveUsers() {
        return userDAO.findAllInactive();
    }

    /**
     * Récupérer les utilisateurs par rôle
     */
    public List<User> getUsersByRole(String roleName) {
        return userDAO.findByRole(roleName);
    }

    /**
     * Mettre à jour un utilisateur
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si le nouveau username existe déjà (sauf si c'est le même)
        if (!updatedUser.getUsername().equals(existingUser.getUsername()) &&
                userDAO.usernameExists(updatedUser.getUsername())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }

        // Mise à jour des champs
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setActive(updatedUser.getActive());

        return userDAO.update(existingUser);
    }

    /**
     * Changer le mot de passe d'un utilisateur
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        String hashedOldPassword = hashPassword(oldPassword);
        if (!user.getPassword().equals(hashedOldPassword)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        // Mettre à jour avec le nouveau mot de passe
        user.setPassword(hashPassword(newPassword));
        userDAO.update(user);
    }

    /**
     * Réinitialiser le mot de passe (par admin)
     */
    public void resetPassword(Long userId, String newPassword) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.setPassword(hashPassword(newPassword));
        userDAO.update(user);
    }

    /**
     * Activer/désactiver un utilisateur
     */
    public void toggleUserStatus(Long userId) {
        userDAO.toggleUserStatus(userId);
    }

    /**
     * Ajouter un rôle à un utilisateur
     */
    public void addRoleToUser(Long userId, Long roleId) {
        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec l'ID: " + roleId));

        userDAO.addRole(userId, role);
    }

    /**
     * Retirer un rôle d'un utilisateur
     */
    public void removeRoleFromUser(Long userId, Long roleId) {
        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé avec l'ID: " + roleId));

        userDAO.removeRole(userId, role);
    }

    /**
     * Supprimer un utilisateur
     */
    public boolean deleteUser(Long id) {
        if (!userDAO.exists(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
        }
        return userDAO.deleteById(id);
    }

    /**
     * Vérifier si un username existe
     */
    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    /**
     * Rafraîchir le token d'accès avec un refresh token
     */
    public AuthenticationResult refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token invalide ou expiré");
        }

        Long userId = jwtService.getUserIdFromToken(refreshToken);
        String username = jwtService.getUsernameFromToken(refreshToken);

        Optional<User> userOpt = userDAO.findById(userId);

        if (userOpt.isPresent() && userOpt.get().getActive()) {
            User user = userOpt.get();

            // Extraire les rôles
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            // Extraire les permissions
            List<String> permissions = permissionDAO.findByUserId(user.getId())
                    .stream()
                    .map(Permission::getName)
                    .distinct()
                    .collect(Collectors.toList());

            // Générer un nouveau access token
            String newAccessToken = jwtService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    roles,
                    permissions
            );

            return new AuthenticationResult(user, newAccessToken, refreshToken);
        }

        throw new IllegalArgumentException("Utilisateur introuvable ou inactif");
    }

    /**
     * Hasher un mot de passe avec SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    public static class AuthenticationResult {
        private final User user;
        private final String accessToken;
        private final String refreshToken;

        public AuthenticationResult(User user, String accessToken, String refreshToken) {
            this.user = user;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public User getUser() { return user; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
    }
}