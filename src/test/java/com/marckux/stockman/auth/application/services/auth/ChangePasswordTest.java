package com.marckux.stockman.auth.application.services.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import com.marckux.stockman.auth.application.dtos.ChangePasswordCommand;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordTest extends BaseTest {

  private static final String HASHED_OLD = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";
  private static final String HASHED_NEW = "$2a$10$E0d6/kuo6re.6M1Yd7nX9eIl1l5xQw7M9VqFq8v0f2rK0QxWg0H4K";

  @Mock
  private PasswordHasherPort passwordHasher;

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private ChangePassword changePassword;

  @Test
  @DisplayName("Debería cambiar la password del usuario activo")
  void shouldChangePasswordForActiveUser() {
    String email = "user@example.mail";
    String currentPassword = "OldPass1";
    String newPassword = "NewPass1";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED_OLD))
      .role(Role.USER)
      .isActive(true)
      .build();
    ChangePasswordCommand command = new ChangePasswordCommand(email, currentPassword, newPassword);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordHasher.matches(currentPassword, HASHED_OLD)).thenReturn(true);
    when(passwordHasher.encode(newPassword)).thenReturn(HASHED_NEW);
    when(userRepository.save(any(User.class))).thenReturn(user);

    changePassword.execute(command);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User saved = userCaptor.getValue();
    assertNotEquals(HASHED_OLD, saved.getHashedPassword().getValue());
    assertEquals(HASHED_NEW, saved.getHashedPassword().getValue());
  }

  @Test
  @DisplayName("Debería rechazar el cambio si la password actual no coincide")
  void shouldRejectWhenCurrentPasswordIsInvalid() {
    String email = "user@example.mail";
    String currentPassword = "WrongPass1";
    String newPassword = "NewPass1";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED_OLD))
      .role(Role.USER)
      .isActive(true)
      .build();
    ChangePasswordCommand command = new ChangePasswordCommand(email, currentPassword, newPassword);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordHasher.matches(currentPassword, HASHED_OLD)).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> changePassword.execute(command));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Debería rechazar el cambio si el usuario está inactivo")
  void shouldRejectWhenUserIsInactive() {
    String email = "user@example.mail";
    String currentPassword = "OldPass1";
    String newPassword = "NewPass1";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED_OLD))
      .role(Role.USER)
      .isActive(false)
      .build();
    ChangePasswordCommand command = new ChangePasswordCommand(email, currentPassword, newPassword);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    assertThrows(InvalidAttributeException.class, () -> changePassword.execute(command));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Debería fallar si el usuario no existe")
  void shouldFailWhenUserNotFound() {
    String email = "missing@example.mail";
    ChangePasswordCommand command = new ChangePasswordCommand(email, "OldPass1", "NewPass1");

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> changePassword.execute(command));
    verify(userRepository, never()).save(any(User.class));
  }
}
