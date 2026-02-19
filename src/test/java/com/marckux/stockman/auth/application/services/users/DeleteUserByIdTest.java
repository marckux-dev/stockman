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

import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
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
  @DisplayName("Debería eliminar un usuario que no es SUPER_ADMIN")
  void shouldDeleteNonSuperAdminUser() {
    UUID id = UUID.randomUUID();
    User user = User.builder()
      .id(id)
      .email(Email.of("user@example.mail"))
      .hashedPassword(HashedPassword.of(HASHED))
      .role(Role.USER)
      .isActive(true)
      .build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    deleteUserById.execute(id);

    verify(userRepository).deleteById(id);
  }

  @Test
  @DisplayName("No debería eliminar un SUPER_ADMIN")
  void shouldRejectWhenUserIsSuperAdmin() {
    UUID id = UUID.randomUUID();
    User user = User.builder()
      .id(id)
      .email(Email.of("super_admin@example.mail"))
      .hashedPassword(HashedPassword.of(HASHED))
      .role(Role.SUPER_ADMIN)
      .isActive(true)
      .build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    assertThrows(InvalidAttributeException.class, () -> deleteUserById.execute(id));
    verify(userRepository, never()).deleteById(id);
  }

  @Test
  @DisplayName("Debería fallar si el usuario no existe")
  void shouldFailWhenUserNotFound() {
    UUID id = UUID.randomUUID();

    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> deleteUserById.execute(id));
    verify(userRepository, never()).deleteById(id);
  }
}
