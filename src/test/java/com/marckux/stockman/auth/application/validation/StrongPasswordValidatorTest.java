package com.marckux.stockman.auth.application.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.marckux.stockman.shared.BaseTest;

public class StrongPasswordValidatorTest extends BaseTest {
  private final StrongPasswordValidator validator = new StrongPasswordValidator();

  @ParameterizedTest
  @ValueSource(strings = {
      "Abcd1234", // Válida
      "Password01", // Válida
      "Stockman2026" // Válida
  })

  @DisplayName("Debería validar positivamente contraseñas fuertes")
  void shouldValidateStrongPasswords(String password) {
    assertTrue(validator.isValid(password, null));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "Abc123", // muy corta
    "Abcdefgh", // sin números
    "ABCD1234", // sin minúsculas
    "abcd1234", // sin mayúsculas
  })
  void shoulNotValidateWeakPasswords(String password) {
    assertFalse(validator.isValid(password, null));
  }

}
