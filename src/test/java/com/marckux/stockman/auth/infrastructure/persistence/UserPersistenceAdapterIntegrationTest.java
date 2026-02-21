package com.marckux.stockman.auth.infrastructure.persistence;

import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;

import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // 1. Carga solo JPA, Hibernate y DataSource. Muy rápido.
@Import(UserPersistenceAdapter.class) // 2. ¡CRUCIAL! Importamos explícitamente tu adaptador manual.
@ActiveProfiles("test") // Usa H2
class UserPersistenceAdapterIntegrationTest {

  private static String HASHED = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

  @Autowired
  private UserPersistenceAdapter adapter;

  @Autowired
  private JpaUserRepository jpaRepository; // Inyectamos el repo de Spring para verificar "la verdad"
  //
  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("Persistencia: Debería guardar un Usuario de Dominio y poder recuperarlo")
  void shouldSaveAndRetrieveUser() {
    // GIVEN: Un usuario puro de dominio
    User domainUser = User.builder()
        .id(null)
        .email(Email.of("adapter_test@stockman.com"))
        .hashedPassword(HashedPassword.of(HASHED))
        .role(Role.ADMIN)
        .activationStatus(ActivationStatus.ACTIVE)
        .build();

    // WHEN: El adaptador lo guarda
    User savedUser = adapter.save(domainUser);

    // THEN:
    // 1. Verificamos que el retorno del adaptador sea correcto
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail().getValue()).isEqualTo("adapter_test@stockman.com");

    // 2. Verificamos "La Verdad" en la DB directamente (saltándonos el adaptador
    // para confirmar)
    // Esto asegura que el adaptador no nos esté mintiendo.
    var entityInDb = jpaRepository.findById(savedUser.getId()).orElseThrow();
    assertThat(entityInDb.getEmail()).isEqualTo("adapter_test@stockman.com");
    assertThat(entityInDb.getRole()).isEqualTo("ADMIN");
  }

  @Test
  @DisplayName("Persistencia: Debería lanzar excepción de DB al violar constraint de Email Único")
  void shouldThrowExceptionOnDuplicateEmail() {
    // GIVEN: Un usuario ya guardado
    User user1 = User.builder()
        .id(null)
        .email(Email.of("unique@stockman.com"))
        .hashedPassword(HashedPassword.of(HASHED))
        .build();
    adapter.save(user1);
    entityManager.flush();
    entityManager.clear();

    // WHEN: Intentamos guardar otro usuario con el MISMO email
    // Nota: Aunque el dominio valida, la DB es la última línea de defensa.
    User user2 = User.builder()
        .id(null)
        .email(Email.of("unique@stockman.com")) // Duplicado
        .hashedPassword(HashedPassword.of(HASHED))
        .build();

    // THEN: Esperamos que la base de datos (H2 en este caso) grite.
    // DataIntegrityViolationException es la excepción de Spring para errores de
    // constraints SQL.
    assertThatThrownBy(() -> {
      adapter.save(user2);
      entityManager.flush();
    })
        .isInstanceOf(PersistenceException.class);
  }

  @Test
  @DisplayName("Persistencia: findByEmail debería mapear correctamente a Objeto de Dominio")
  void shouldFindDomainUserByEmail() {
    // GIVEN: Datos insertados directamente en el repo JPA (bypass del adapter para
    // preparar el escenario)
    UserEntity entity = UserEntity.builder()
        .email("found@stockman.com")
        .password(HASHED)
        .role("USER")
        .activationStatus(ActivationStatus.INACTIVE) // Probamos un flag false
        .build();
    jpaRepository.save(entity);

    // WHEN: El adaptador busca
    Optional<User> result = adapter.findByEmail("found@stockman.com");

    // THEN: Debe reconstruir el objeto de dominio perfectamente
    assertThat(result).isPresent();
    User user = result.get();
    assertThat(user.isActive()).isFalse(); // Verificamos que el booleano se mapeó bien
    assertThat(user.getRole()).isEqualTo(Role.USER); // Verificamos el Enum
    assertThat(user.getEmail().getValue()).isEqualTo("found@stockman.com"); // Verificamos el Value Object
  }
}
