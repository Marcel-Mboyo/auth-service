package com.archer.cbs.authservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public class RoleDTO {

    private Long id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom du rôle doit contenir entre 3 et 50 caractères")
    private String name;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Set<String> permissions;

    // Constructeurs
    public RoleDTO() {}

    public RoleDTO(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<String> getPermissions() { return permissions; }

    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
