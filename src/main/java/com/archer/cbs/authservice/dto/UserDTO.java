package com.archer.cbs.authservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.archer.cbs.authservice.entity.Person;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UserDTO {
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private PersonDTO person;

    private Set<String> roles;

    // Constructeurs
    public UserDTO() {}

    public UserDTO(Long id, String username, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.active = active;
        this.createdAt = createdAt;
    }

    public UserDTO(Long id, String username, Boolean active, LocalDateTime createdAt, Person person) {
        this.id = id;
        this.username = username;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public PersonDTO getPerson() { return this.person; }

    public void setPerson(PersonDTO person) { this.person = person; }

    public Set<String> getRoles() { return roles; }

    public void setRoles(Set<String> roles) { this.roles = roles; }
}