package com.marckux.stockman.auth.application.services.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
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

import com.marckux.stockman.auth.application.dtos.BlockUserCommand;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class BlockUserTest extends BaseTest {

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private BlockUser blockUser;

  @Test
  @DisplayName("ADMIN puede bloquear USER")
  void shouldAllowAdminToBlockUser() {
    UUID targetId = UUID.randomUUID();
    User requester = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of("admin@example.com"))
      .role(Role.ADMIN)
      .activationStatus(ActivationStatus.ACTIVE)
      .hashedPassword(com.marckux.stockman.auth.domain.model.vo.HashedPassword.of(
        "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a"))
      .build();
    User target = User.builder()
      .id(targetId)
      .email(Email.of("user@example.com"))
      .role(Role.USER)
      .activationStatus(ActivationStatus.ACTIVE)
      .hashedPassword(com.marckux.stockman.auth.domain.model.vo.HashedPassword.of(
        "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a"))
      .build();

    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    var response = blockUser.execute(new BlockUserCommand(targetId, "admin@example.com"));
    assertEquals(ActivationStatus.BLOCKED.name(), response.activationStatus());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("ADMIN no puede bloquear ADMIN")
  void shouldRejectAdminBlockingAdmin() {
    UUID targetId = UUID.randomUUID();
    User requester = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of("admin@example.com"))
      .role(Role.ADMIN)
      .activationStatus(ActivationStatus.ACTIVE)
      .hashedPassword(com.marckux.stockman.auth.domain.model.vo.HashedPassword.of(
        "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a"))
      .build();
    User target = User.builder()
      .id(targetId)
      .email(Email.of("admin2@example.com"))
      .role(Role.ADMIN)
      .activationStatus(ActivationStatus.ACTIVE)
      .hashedPassword(com.marckux.stockman.auth.domain.model.vo.HashedPassword.of(
        "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a"))
      .build();

    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    assertThrows(InvalidAttributeException.class,
      () -> blockUser.execute(new BlockUserCommand(targetId, "admin@example.com")));
  }
}
