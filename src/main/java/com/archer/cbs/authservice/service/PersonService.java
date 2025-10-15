package com.archer.cbs.authservice.service;

import com.archer.cbs.authservice.dao.PersonDAO;
import com.archer.cbs.authservice.dto.ApiResponse;
import com.archer.cbs.authservice.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
@Transactional
public class PersonService {

    @Inject
    private PersonDAO personDAO;

    /**
     * Créer une nouvelle personne
     */
    public Person createPerson(Person person) {
        // Vérifier si l'email existe déjà
        if (person.getEmail() != null && personDAO.emailExists(person.getEmail())) {
            throw new IllegalArgumentException("Une personne avec cet email existe déjà");
        }

        return personDAO.create(person);
    }

    /**
     * Récupérer une personne par ID
     */
    public Optional<Person> getPersonById(Long id) {
        return personDAO.findById(id);
    }

    /**
     * Récupérer une personne par email
     */
    public Optional<Person> getPersonByEmail(String email) {
        return personDAO.findByEmail(email);
    }

    /**
     * Récupérer une personne par téléphone
     */
    public Optional<Person> getPersonByTelephone(String phone) {
        return personDAO.findByPhone(phone);
    }

    /**
     * Récupérer toutes les personnes
     */
    public List<Person> getAllPersons() {
        return personDAO.findAll();
    }

    /**
     * Rechercher des personnes par nom
     */
    public List<Person> searchPersonsByNom(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }
        return personDAO.searchByFirstName(firstName);
    }

    /**
     * Mettre à jour une personne
     */
    public Person updatePerson(Long id, Person updatedPerson) {
        Person existingPerson = personDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personne non trouvée avec l'ID: " + id));

        // Vérifier si le nouvel email existe déjà (sauf si c'est le même)
        if (updatedPerson.getEmail() != null &&
                !updatedPerson.getEmail().equals(existingPerson.getEmail()) &&
                personDAO.emailExists(updatedPerson.getEmail())) {
            throw new IllegalArgumentException("Une personne avec cet email existe déjà");
        }

        // Mise à jour des champs
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setLastName(updatedPerson.getLastName());
        existingPerson.setEmail(updatedPerson.getEmail());
        existingPerson.setPhone(updatedPerson.getPhone());
        existingPerson.setBirthDate(updatedPerson.getBirthDate());

        return personDAO.update(existingPerson);
    }

    /**
     * Supprimer une personne
     */
    public boolean deletePerson(Long id) {
        if (!personDAO.exists(id)) {
            throw new IllegalArgumentException("Personne non trouvée avec l'ID: " + id);
        }
        return personDAO.deleteById(id);
    }

    /**
     * Compter le nombre total de personnes
     */
    public Long countPersons() {
        return personDAO.count();
    }

    /**
     * Vérifier si une personne existe
     */
    public boolean personExists(Long id) {
        return personDAO.exists(id);
    }

    /**
     * Vérifier si un email existe
     */
    public boolean emailExists(String email) {
        return personDAO.emailExists(email);
    }
}