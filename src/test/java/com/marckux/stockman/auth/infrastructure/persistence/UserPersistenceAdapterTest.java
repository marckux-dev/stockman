package com.marckux.stockman.auth.infrastructure.persistence;

import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.shared.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest extends BaseTest {

  @Mock
  private JpaUserRepository jpaUserRepository;

  @InjectMocks
  private UserPersistenceAdapter adapter;

  private final String VALID_HASH = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

  @Test
  @DisplayName("Mapper: Debería convertir Entity -> Domain correctamente")
  void shouldMapEntityToDomain() {
    // GIVEN
    UserEntity entity = UserEntity.builder()
        .id(UUID.randomUUID())
        .email("test@test.com")
        .password(VALID_HASH)
        .role("ADMIN")
        .activationStatus(ActivationStatus.ACTIVE)
        .build();

    when(jpaUserRepository.findByEmail("test@test.com")).thenReturn(Optional.of(entity));

    // WHEN
    Optional<User> result = adapter.findByEmail("test@test.com");

    // THEN
    assertTrue(result.isPresent());
    User user = result.get();

    // Verificamos que los Value Objects se han reconstruido bien
    assertEquals("test@test.com", user.getEmail().getValue());
    assertEquals(VALID_HASH, user.getHashedPassword().getValue());
    assertEquals(Role.ADMIN, user.getRole());
  }

  @Test
  @DisplayName("Mapper: Debería convertir Domain -> Entity correctamente al guardar")
  void shouldMapDomainToEntity() {
    // GIVEN
    User domainUser = User.builder()
        .email(Email.of("test@test.com"))
        .hashedPassword(HashedPassword.of(VALID_HASH))
        .role(Role.USER)
        .activationStatus(ActivationStatus.ACTIVE)
        .build();

    // Simulamos que al guardar devuelve la misma entidad (con ID generado)
    when(jpaUserRepository.save(any(UserEntity.class))).thenAnswer(i -> {
      UserEntity e = i.getArgument(0);
      e.setId(UUID.randomUUID());
      return e;
    });

    // WHEN
    adapter.save(domainUser);

    // THEN
    // Verificamos que al repositorio JPA le llegó la entidad con los datos crudos
    // extraídos de los VOs
    verify(jpaUserRepository)
        .save(org.mockito.ArgumentMatchers.argThat(entity -> entity.getEmail().equals("test@test.com") &&
            entity.getPassword().equals(VALID_HASH) &&
            entity.getRole().equals("USER")));
  }
}
