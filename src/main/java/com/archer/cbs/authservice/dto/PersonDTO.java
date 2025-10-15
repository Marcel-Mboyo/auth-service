package com.archer.cbs.authservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;


public class PersonDTO {

    private Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String lastName;

    @Email(message = "L'adresse email est invalide")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone est trop long")
    private String phone;

    @Past(message = "La date de naissance doit être dans le passé")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructeurs
    public PersonDTO() {}

    public PersonDTO(Long id, String firstName, String lastName, String email, String phone,
                     LocalDate birthDate, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String nom) { this.firstName = nom; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String telephone) { this.phone = telephone; }

    public LocalDate getBirthDate() { return birthDate; }

    public void setBirthDate(LocalDate dateNaissance) { this.birthDate = dateNaissance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}