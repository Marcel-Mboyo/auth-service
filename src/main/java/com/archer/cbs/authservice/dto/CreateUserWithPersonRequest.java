package com.archer.cbs.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserWithPersonRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 16, message = "Le mot de passe doit contenir entre 6 et 16 caractères")
    private String password;

    @NotNull(message = "La personne est obligatoire")
    private PersonDTO person;

    // Constructeurs
    public CreateUserWithPersonRequest() {}

    public CreateUserWithPersonRequest(String username, String password, PersonDTO person) {
        this.username = username;
        this.password = password;
        this.person = person;
    }

    // Getters et Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public PersonDTO getPerson() { return person; }
    public void setPerson(PersonDTO person) { this.person = person; }
}
