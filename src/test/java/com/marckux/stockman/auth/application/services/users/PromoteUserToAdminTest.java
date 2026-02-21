package com.marckux.stockman.auth.application.services.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class PromoteUserToAdminTest extends BaseTest {

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private PromoteUserToAdmin service;

  @Test
  @DisplayName("Promueve USER a ADMIN")
  void shouldPromoteUserToAdmin() {
    UUID id = UUID.randomUUID();
    User user = User.builder()
      .id(id)
      .email(Email.of("user@example.com"))
      .role(Role.USER)
      .activationStatus(ActivationStatus.INACTIVE)
      .build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(i -> i.getArgument(0));

    var result = service.execute(id);
    assertEquals(Role.ADMIN.name(), result.role());
  }

  @Test
  @DisplayName("Rechaza promover si rol no es USER")
  void shouldRejectIfRoleIsNotUser() {
    UUID id = UUID.randomUUID();
    User admin = User.builder()
      .id(id)
      .email(Email.of("admin@example.com"))
      .role(Role.ADMIN)
      .activationStatus(ActivationStatus.INACTIVE)
      .build();

    when(userRepository.findById(id)).thenReturn(Optional.of(admin));

    assertThrows(InvalidAttributeException.class, () -> service.execute(id));
    verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
  }
}
