package com.archer.cbs.authservice.dao;

import com.archer.cbs.authservice.entity.Person;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

@Stateless
public class PersonDAO extends BaseDAO<Person> {

    public PersonDAO() {
        super(Person.class);
    }

    /**
     * Trouver une personne par email
     */
    public Optional<Person> findByEmail(String email) {
        try {
            String jpql = "SELECT p FROM Person p WHERE p.email = :email";
            TypedQuery<Person> query = entityManager.createQuery(jpql, Person.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouver une personne par téléphone
     */
    public Optional<Person> findByPhone(String phone) {
        try {
            String jpql = "SELECT p FROM Person p WHERE p.phone = :phone";
            TypedQuery<Person> query = entityManager.createQuery(jpql, Person.class);
            query.setParameter("phone", phone);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Rechercher des personnes par nom (partiel)
     */
    public java.util.List<Person> searchByFirstName(String firstName) {
        String jpql = "SELECT p FROM Person p WHERE LOWER(p.firstName) LIKE LOWER(:firstName)";
        TypedQuery<Person> query = entityManager.createQuery(jpql, Person.class);
        query.setParameter("firstName", "%" + firstName + "%");
        return query.getResultList();
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean emailExists(String email) {
        String jpql = "SELECT COUNT(p) FROM Person p WHERE p.email = :email";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
