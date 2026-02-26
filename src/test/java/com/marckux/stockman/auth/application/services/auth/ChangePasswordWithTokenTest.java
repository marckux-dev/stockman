package com.marckux.stockman.auth.application.services.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.auth.application.dtos.ChangePasswordWithTokenCommand;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.exceptions.InvalidTokenException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ChangePasswordWithTokenTest extends BaseTest {

  private static final String HASHED_NEW = "$2a$10$E0d6/kuo6re.6M1Yd7nX9eIl1l5xQw7M9VqFq8v0f2rK0QxWg0H4K";

  @Mock
  private PasswordHasherPort passwordHasher;

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private ChangePasswordWithToken changePasswordWithToken;

  @Test
  @DisplayName("Debería asignar password, activar usuario y eliminar token")
  void shouldSetPasswordUsingValidToken() {
    String token = "valid-token";
    String newPassword = "NewPass1";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of("user@example.mail"))
      .role(Role.USER)
      .activationStatus(ActivationStatus.INACTIVE)
      .token(token)
      .tokenExpiration(Instant.now().plusSeconds(300))
      .build();

    when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
    when(passwordHasher.encode(newPassword)).thenReturn(HASHED_NEW);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    changePasswordWithToken.execute(new ChangePasswordWithTokenCommand(token, newPassword));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User saved = userCaptor.getValue();
    assertEquals(HASHED_NEW, saved.getHashedPassword().getValue());
    assertEquals(ActivationStatus.ACTIVE, saved.getActivationStatus());
    assertEquals(null, saved.getToken());
    assertEquals(null, saved.getTokenExpiration());
  }

  @Test
  @DisplayName("Debería rechazar token expirado")
  void shouldRejectExpiredToken() {
    String token = "expired-token";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of("user@example.mail"))
      .role(Role.USER)
      .activationStatus(ActivationStatus.INACTIVE)
      .token(token)
      .tokenExpiration(Instant.now().minusSeconds(1))
      .build();

    when(userRepository.findByToken(token)).thenReturn(Optional.of(user));

    assertThrows(InvalidTokenException.class, () -> changePasswordWithToken.execute(new ChangePasswordWithTokenCommand(token, "NewPass1")));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Debería fallar si el token no existe")
  void shouldFailWhenTokenNotFound() {
    String token = "missing-token";
    when(userRepository.findByToken(token)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> changePasswordWithToken.execute(new ChangePasswordWithTokenCommand(token, "NewPass1")));
    verify(userRepository, never()).save(any(User.class));
  }
}
