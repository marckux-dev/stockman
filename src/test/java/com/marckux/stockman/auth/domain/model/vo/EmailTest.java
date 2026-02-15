package com.marckux.stockman.auth.domain.model.vo;

import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest extends BaseTest {

    @Test
    @DisplayName("Debería crear un Email válido")
    void shouldCreateValidEmail() {
        String validEmail = "test@example.com";
        Email email = new Email(validEmail);
        assertEquals(validEmail, email.getValue());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción con formatos inválidos")
    @ValueSource(strings = {"test", "test@", "test@com", "test.com", ""})
    void shouldThrowExceptionForInvalidFormats(String invalidEmail) {
        assertThrows(InvalidAttributeException.class, () -> new Email(invalidEmail));
    }

    @Test
    @DisplayName("Debería lanzar excepción si es nulo")
    void shouldThrowExceptionIfNull() {
        assertThrows(InvalidAttributeException.class, () -> new Email(null));
    }
    
    @Test
    @DisplayName("Dos emails con el mismo valor deberían ser iguales (EqualsAndHashCode)")
    void shouldRespectEquality() {
        assertEquals(new Email("a@a.com"), new Email("a@a.com"));
    }
}
