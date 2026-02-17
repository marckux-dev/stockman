package com.marckux.stockman.auth.application.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
class UniqueEmailValidatorTest extends BaseTest {

  private static String HASH = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";
  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private UniqueEmailValidator validator;

  @Test
  @DisplayName("Debería devolver false si el email ya existe en el sistema")
  void shouldReturnFalseIfEmailAlreadyExists() {
    // GIVEN
    String email = "duplicate@test.com";
    // Simulamos que el repositorio SÍ encuentra a alguien
    when(userRepository.findByEmail(email))
        .thenReturn(Optional.of(User.builder()
            .email(Email.of(email))
            .hashedPassword(HashedPassword.of(HASH))
            .build()));

    // WHEN
    boolean isValid = validator.isValid(email, null);

    // THEN
    assertFalse(isValid, "El validador debería rechazar un email duplicado");
  }

  @Test
  @DisplayName("Debería devolver true si el email no existe")
  void shouldReturnTrueIdEmailDoesNotExist() {
    String email = "valid@mail.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    assertTrue(validator.isValid(email, null),
      "El validador debería aceptar un email único"
    );
  }

  @Test
  @DisplayName("Debería devolver true si el email es nulo")
  void shouldReturnTrueIdEmailIsNull() {
    assertTrue(validator.isValid(null, null),
      "El validador debería aceptar un email único"
    );
  }

}
