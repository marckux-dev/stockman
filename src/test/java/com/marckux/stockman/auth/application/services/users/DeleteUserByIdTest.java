package com.marckux.stockman.auth.application.services.users;

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

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.application.ports.in.usecases.DeleteUserByIdUseCase;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class DeleteUserByIdTest extends BaseTest {

  private static final String HASHED = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private DeleteUserById deleteUserById;

  @Test
  @DisplayName("ADMIN debería eliminar un USER")
  void shouldAllowAdminToDeleteUser() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "admin@example.mail";
    User requester = buildUser(UUID.randomUUID(), requesterEmail, Role.ADMIN);
    User target = buildUser(targetId, "user@example.mail", Role.USER);

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail));

    verify(userRepository).deleteById(targetId);
  }

  @Test
  @DisplayName("ADMIN no debería eliminar un ADMIN")
  void shouldRejectAdminDeletingAdmin() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "admin@example.mail";
    User requester = buildUser(UUID.randomUUID(), requesterEmail, Role.ADMIN);
    User target = buildUser(targetId, "other-admin@example.mail", Role.ADMIN);

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    assertThrows(InvalidAttributeException.class, () ->
      deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail))
    );
    verify(userRepository, never()).deleteById(targetId);
  }

  @Test
  @DisplayName("SUPER_ADMIN puede eliminar un ADMIN")
  void shouldAllowSuperAdminToDeleteAdmin() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "super_admin@example.mail";
    User requester = buildUser(UUID.randomUUID(), requesterEmail, Role.SUPER_ADMIN);
    User target = buildUser(targetId, "admin@example.mail", Role.ADMIN);

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail));

    verify(userRepository).deleteById(targetId);
  }

  @Test
  @DisplayName("No debería eliminar un SUPER_ADMIN")
  void shouldRejectWhenUserIsSuperAdmin() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "super_admin@example.mail";
    User requester = buildUser(UUID.randomUUID(), requesterEmail, Role.SUPER_ADMIN);
    User target = buildUser(targetId, "other-super@example.mail", Role.SUPER_ADMIN);

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    assertThrows(InvalidAttributeException.class, () ->
      deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail))
    );
    verify(userRepository, never()).deleteById(targetId);
  }

  @Test
  @DisplayName("Debería fallar si el usuario solicitante no existe")
  void shouldFailWhenRequesterNotFound() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "missing@example.mail";

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail))
    );
    verify(userRepository, never()).deleteById(targetId);
  }

  @Test
  @DisplayName("Debería fallar si el usuario objetivo no existe")
  void shouldFailWhenTargetNotFound() {
    UUID targetId = UUID.randomUUID();
    String requesterEmail = "admin@example.mail";
    User requester = buildUser(UUID.randomUUID(), requesterEmail, Role.ADMIN);

    when(userRepository.findByEmail(requesterEmail)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
      deleteUserById.execute(new DeleteUserByIdUseCase.Input(targetId, requesterEmail))
    );
    verify(userRepository, never()).deleteById(targetId);
  }

  private User buildUser(UUID id, String email, Role role) {
    return User.builder()
      .id(id)
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED))
      .role(role)
      .activationStatus(ActivationStatus.ACTIVE)
      .build();
  }
}
