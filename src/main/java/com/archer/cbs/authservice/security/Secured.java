package com.archer.cbs.authservice.security;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour sécuriser les endpoints
 * Peut s'appliquer sur une classe ou une méthode
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
    /**
     * Rôles autorisés (OU logique)
     */
    String[] roles() default {};

    /**
     * Permissions requises (OU logique)
     */
    String[] permissions() default {};
}