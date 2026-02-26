package com.marckux.stockman.stock.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;
import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;
import com.marckux.stockman.stock.application.dtos.UpdateCenterRequest;
/**
 * Tests de integración para el CRUD de Centers.
 */
public class CenterIntegrationTest extends IntegrationBaseTest {

  /**
   * Verifica CRUD completo con SUPER_ADMIN.
   */
  @Test
  @DisplayName("CRUD de centers por SUPER_ADMIN")
  void shouldHandleCrudForSuperAdmin() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");

    CenterResponse created = superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro Norte"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(CenterResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(created).isNotNull();
    assertThat(created.name()).isEqualTo("Centro Norte");

    String id = created.id();

    superAdminClient.get()
      .uri("/api/stock/centers/{id}", id)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.name").isEqualTo("Centro Norte");

    superAdminClient.get()
      .uri("/api/stock/centers")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].name").exists();

    superAdminClient.put()
      .uri("/api/stock/centers/{id}", id)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new UpdateCenterRequest("Centro Sur"))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.name").isEqualTo("Centro Sur")
      .jsonPath("$.isActive").isEqualTo(true);

    superAdminClient.delete()
      .uri("/api/stock/centers/{id}", id)
      .exchange()
      .expectStatus().isNoContent();

    superAdminClient.get()
      .uri("/api/stock/centers/{id}", id)
      .exchange()
      .expectStatus().isNotFound();
  }

  /**
   * Verifica rechazo sin autenticación.
   */
  @Test
  @DisplayName("Debe rechazar acceso sin token")
  void shouldRejectWithoutToken() {
    webTestClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro X"))
      .exchange()
      .expectStatus().isForbidden();

    webTestClient.get()
      .uri("/api/stock/centers")
      .exchange()
      .expectStatus().isForbidden();
  }

  /**
   * Verifica rechazo por nombre duplicado.
   */
  @Test
  @DisplayName("Debe rechazar nombre duplicado")
  void shouldRejectDuplicateName() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");

    superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro Unico"))
      .exchange()
      .expectStatus().isCreated();

    superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro Unico"))
      .exchange()
      .expectStatus().isBadRequest();
  }

  /**
   * Verifica que READ requiere autenticación.
   */
  @Test
  @DisplayName("READ requiere autenticación, pero no SUPER_ADMIN")
  void shouldAllowReadForAuthenticatedUser() {
    var authClient = webTestClientWithUser("user", "USER");

    authClient.get()
      .uri("/api/stock/centers")
      .exchange()
      .expectStatus().isOk();
  }
}
