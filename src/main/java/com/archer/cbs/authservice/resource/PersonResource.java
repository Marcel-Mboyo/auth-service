package com.archer.cbs.authservice.resource;


import com.archer.cbs.authservice.dto.ApiResponse;
import com.archer.cbs.authservice.dto.PersonDTO;
import com.archer.cbs.authservice.entity.Person;
import com.archer.cbs.authservice.mapper.EntityMapper;
import com.archer.cbs.authservice.service.PersonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    private PersonService personService;

    /**
     * Créer une nouvelle personne
     * POST /api/persons
     */
    @POST
    public Response createPerson(@Valid PersonDTO personDTO) throws JsonProcessingException {

        try {
            Person person = EntityMapper.toPerson(personDTO);
            Person created = personService.createPerson(person);
            PersonDTO result = EntityMapper.toPersonDTO(created);

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Personne créée avec succès", result))
                    .build();

        }  catch (IllegalArgumentException e) {

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
     * Récupérer toutes les personnes
     * GET /api/persons
     */
    @GET
    public Response getAllPersons() {
        try {
            List<PersonDTO> persons = personService.getAllPersons()
                    .stream()
                    .map(EntityMapper::toPersonDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Liste des personnes", persons))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer une personne par ID
     * GET /api/persons/{id}
     */
    @GET
    @Path("/{id}")
    public Response getPersonById(@PathParam("id") Long id) {
        try {
            return personService.getPersonById(id)
                    .map(person -> Response.ok(
                            ApiResponse.success("Personne trouvée", EntityMapper.toPersonDTO(person))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Personne non trouvée"))
                            .build());

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Récupérer une personne par email
     * GET /api/persons/email/{email}
     */
    @GET
    @Path("/email/{email}")
    public Response getPersonByEmail(@PathParam("email") String email) {
        try {
            return personService.getPersonByEmail(email)
                    .map(person -> Response.ok(
                            ApiResponse.success("Personne trouvée", EntityMapper.toPersonDTO(person))
                    ).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity(ApiResponse.error("Personne non trouvée"))
                            .build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Rechercher des personnes par nom
     * GET /api/persons/search?nom=xxx
     */
    @GET
    @Path("/search")
    public Response searchPersons(@QueryParam("nom") String nom) {
        try {
            List<PersonDTO> persons = personService.searchPersonsByNom(nom)
                    .stream()
                    .map(EntityMapper::toPersonDTO)
                    .collect(Collectors.toList());

            return Response.ok(ApiResponse.success("Résultats de recherche", persons))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors de la recherche: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Mettre à jour une personne
     * PUT /api/persons/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updatePerson(@PathParam("id") Long id, PersonDTO personDTO) {
        try {
            Person person = EntityMapper.toPerson(personDTO);
            Person updated = personService.updatePerson(id, person);
            PersonDTO result = EntityMapper.toPersonDTO(updated);

            return Response.ok(ApiResponse.success("Personne mise à jour avec succès", result))
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
     * Supprimer une personne
     * DELETE /api/persons/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") Long id) {
        try {
            personService.deletePerson(id);
            return Response.ok(ApiResponse.success("Personne supprimée avec succès"))
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

    /**
     * Compter le nombre de personnes
     * GET /api/persons/count
     */
    @GET
    @Path("/count")
    public Response countPersons() {
        try {
            Long count = personService.countPersons();
            return Response.ok(ApiResponse.success("Nombre de personnes", count))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Erreur lors du comptage: " + e.getMessage()))
                    .build();
        }
    }
}