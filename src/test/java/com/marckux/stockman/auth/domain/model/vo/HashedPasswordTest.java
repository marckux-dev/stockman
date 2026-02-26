package com.marckux.stockman.auth.domain.model.vo;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashedPasswordTest extends BaseTest {

    // Un hash válido de ejemplo (simulado pero cumple el regex)
    private static final String VALID_HASH = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

    @Test
    @DisplayName("Debería crear HashedPassword si el formato es BCrypt correcto")
    void shouldCreateValidHashedPassword() {
        assertDoesNotThrow(() -> new HashedPassword(VALID_HASH));
    }

    @Test
    @DisplayName("Debería lanzar excepción si intentamos guardar texto plano")
    void shouldThrowExceptionForPlainPassword() {
        assertThrows(InvalidAttributeException.class, () -> new HashedPassword("password123"));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del hash está roto")
    void shouldThrowExceptionForBrokenHash() {
        // Empieza bien pero es muy corto
        assertThrows(InvalidAttributeException.class, () -> new HashedPassword("$2a$10$corto"));
    }
}
