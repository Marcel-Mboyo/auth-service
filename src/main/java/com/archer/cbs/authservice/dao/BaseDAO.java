package com.archer.cbs.authservice.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public abstract class BaseDAO<T> {

    @PersistenceContext(unitName = "AuthPU")
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Créer une nouvelle entité
     */
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * Trouver une entité par ID
     */
    public Optional<T> findById(Long id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    /**
     * Récupérer toutes les entités
     */
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    /**
     * Mettre à jour une entité
     */
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    /**
     * Supprimer une entité
     */
    public void delete(T entity) {
        if (!entityManager.contains(entity)) {
            entity = entityManager.merge(entity);
        }
        entityManager.remove(entity);
    }

    /**
     * Supprimer par ID
     */
    public boolean deleteById(Long id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
            return true;
        }
        return false;
    }

    /**
     * Compter le nombre total d'entités
     */
    public Long count() {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    /**
     * Vérifier si une entité existe
     */
    public boolean exists(Long id) {
        return findById(id).isPresent();
    }
}