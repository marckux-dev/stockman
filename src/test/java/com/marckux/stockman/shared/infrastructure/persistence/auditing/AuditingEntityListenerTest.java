package com.marckux.stockman.shared.infrastructure.persistence.auditing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.marckux.stockman.auth.infrastructure.security.models.AuthUserDetails;
import com.marckux.stockman.stock.infrastructure.persistence.CenterEntity;
import com.marckux.stockman.stock.infrastructure.persistence.JpaCenterRepository;

@DataJpaTest
@ActiveProfiles("test")
class AuditingEntityListenerTest {

  @Autowired
  private JpaCenterRepository centerRepository;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Auditing: debería asignar createdAt y createdBy en creación")
  void shouldSetCreatedAuditFieldsOnPersist() {
    UUID userId = UUID.randomUUID();
    setAuthenticatedUser(userId, "creator@stockman.com");

    CenterEntity entity = CenterEntity.builder()
      .name("Centro Auditado")
      .isActive(true)
      .build();

    CenterEntity saved = centerRepository.saveAndFlush(entity);

    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getCreatedBy()).isEqualTo(userId);
    assertThat(saved.getUpdatedAt()).isNull();
    assertThat(saved.getUpdatedBy()).isNull();
  }

  @Test
  @DisplayName("Auditing: debería asignar updatedAt y updatedBy en actualización")
  void shouldSetUpdatedAuditFieldsOnUpdate() {
    UUID creatorId = UUID.randomUUID();
    setAuthenticatedUser(creatorId, "creator@stockman.com");

    CenterEntity entity = CenterEntity.builder()
      .name("Centro Original")
      .isActive(true)
      .build();

    CenterEntity saved = centerRepository.saveAndFlush(entity);

    UUID updaterId = UUID.randomUUID();
    setAuthenticatedUser(updaterId, "updater@stockman.com");

    saved.setName("Centro Actualizado");
    CenterEntity updated = centerRepository.saveAndFlush(saved);

    assertThat(updated.getCreatedBy()).isEqualTo(creatorId);
    assertThat(updated.getUpdatedAt()).isNotNull();
    assertThat(updated.getUpdatedBy()).isEqualTo(updaterId);
  }

  private void setAuthenticatedUser(UUID userId, String email) {
    AuthUserDetails principal = new AuthUserDetails(
      userId,
      email,
      "{noop}password",
      List.of(new SimpleGrantedAuthority("ROLE_USER")),
      true,
      true,
      true,
      true
    );
    var authentication = new UsernamePasswordAuthenticationToken(
      principal,
      null,
      principal.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
