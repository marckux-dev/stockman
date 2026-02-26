package com.marckux.stockman.auth.application.services.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.marckux.stockman.auth.application.dtos.ChangePasswordWithPasswordCommand;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordWithPasswordTest extends BaseTest {

  private static final String HASHED_OLD = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";
  private static final String HASHED_NEW = "$2a$10$E0d6/kuo6re.6M1Yd7nX9eIl1l5xQw7M9VqFq8v0f2rK0QxWg0H4K";

  @Mock
  private PasswordHasherPort passwordHasher;

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private ChangePasswordWithPassword changePasswordWithPassword;

  @Test
  @DisplayName("Debería actualizar la password cuando la anterior es correcta")
  void shouldUpdatePasswordWhenOldPasswordMatches() {
    String email = "user@example.mail";
    String oldPassword = "OldPass1";
    String newPassword = "NewPass2";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED_OLD))
      .role(Role.USER)
      .activationStatus(ActivationStatus.ACTIVE)
      .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordHasher.matches(oldPassword, HASHED_OLD)).thenReturn(true);
    when(passwordHasher.encode(newPassword)).thenReturn(HASHED_NEW);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    changePasswordWithPassword.execute(new ChangePasswordWithPasswordCommand(email, oldPassword, newPassword));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User saved = userCaptor.getValue();
    assertEquals(HASHED_NEW, saved.getHashedPassword().getValue());
    assertEquals(ActivationStatus.ACTIVE, saved.getActivationStatus());
  }

  @Test
  @DisplayName("Debería rechazar el cambio si el usuario está inactivo")
  void shouldRejectInactiveUser() {
    String email = "inactive@example.mail";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .activationStatus(ActivationStatus.INACTIVE)
      .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    assertThrows(BadCredentialsException.class, () -> changePasswordWithPassword.execute(
      new ChangePasswordWithPasswordCommand(email, "OldPass1", "NewPass2")));

    verify(passwordHasher, never()).matches(any(), any());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Debería rechazar la password anterior incorrecta")
  void shouldRejectInvalidOldPassword() {
    String email = "user@example.mail";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED_OLD))
      .role(Role.USER)
      .activationStatus(ActivationStatus.ACTIVE)
      .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordHasher.matches("OldPass1", HASHED_OLD)).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> changePasswordWithPassword.execute(
      new ChangePasswordWithPasswordCommand(email, "OldPass1", "NewPass2")));

    verify(passwordHasher, never()).encode(any());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Debería fallar si el usuario no existe")
  void shouldFailWhenUserNotFound() {
    String email = "missing@example.mail";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> changePasswordWithPassword.execute(
      new ChangePasswordWithPasswordCommand(email, "OldPass1", "NewPass2")));

    verify(passwordHasher, never()).matches(any(), any());
    verify(userRepository, never()).save(any(User.class));
  }
}
