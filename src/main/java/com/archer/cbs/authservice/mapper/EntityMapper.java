package com.archer.cbs.authservice.mapper;


import com.archer.cbs.authservice.dto.*;
import com.archer.cbs.authservice.entity.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class EntityMapper {

    // Person Mapping
    public static PersonDTO toPersonDTO(Person person) {
        if (person == null) return null;

        return new PersonDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getPhone(),
                person.getBirthDate(),
                person.getCreatedAt()
        );
    }

    public static Person toPerson(PersonDTO dto) {
        if (dto == null) return null;

        Person person = new Person();
        person.setId(dto.getId());
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setBirthDate(dto.getBirthDate());

        return person;
    }

    // User Mapping
    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getActive(),
                user.getCreatedAt(),
                user.getPerson()
        );

        dto.setPerson(toPersonDTO(user.getPerson()));

        if (user.getRoles() != null) {
            dto.setRoles(
                    user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    public static User toUser(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setActive(dto.getActive());
        user.setCreatedAt(dto.getCreatedAt());

        return user;
    }

    // Role Mapping
    public static RoleDTO toRoleDTO(Role role) {
        if (role == null) return null;

        RoleDTO dto = new RoleDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt()
        );

        if (role.getPermissions() != null) {
            dto.setPermissions(
                    role.getPermissions().stream()
                            .map(Permission::getName)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    public static Role toRole(RoleDTO dto) {
        if (dto == null) return null;

        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setCreatedAt(dto.getCreatedAt());

        return role;
    }

    // Permission Mapping
    public static PermissionDTO toPermissionDTO(Permission permission) {
        if (permission == null) return null;

        return new PermissionDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.getCreatedAt()
        );
    }

    public static Permission toPermission(PermissionDTO dto) {
        if (dto == null) return null;

        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        permission.setCreatedAt(dto.getCreatedAt());

        return permission;
    }
}
