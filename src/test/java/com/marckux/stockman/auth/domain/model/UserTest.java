package com.marckux.stockman.auth.domain.model;

import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.shared.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest extends BaseTest {

    private final Email email = new Email("test@test.com");
    private final HashedPassword password = new HashedPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a");

    @Test
    @DisplayName("Debería asignar Role.USER y isActive=true por defecto si no se especifican")
    void shouldAssignDefaults() {
        // WHEN: Creamos usuario sin rol ni active
        User user = User.builder()
                .email(email)
                .hashedPassword(password)
                .name("Pepe")
                .build();

        // THEN
        assertNotNull(user.getRole(), "El rol no debería ser nulo");
        assertEquals(Role.USER, user.getRole(), "El rol por defecto debe ser USER");
        assertTrue(user.getIsActive(), "El usuario debería nacer activo");
    }

    @Test
    @DisplayName("Debería lanzar excepción si faltan datos obligatorios")
    void shouldThrowExceptionIfMissingData() {
        // Sin Email
        assertThrows(InvalidAttributeException.class, () -> User.builder()
                .hashedPassword(password)
                .build());

        // Sin Password
        assertThrows(InvalidAttributeException.class, () -> User.builder()
                .email(email)
                .build());
    }
}
