package com.marckux.stockman.stock.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;
import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;
import com.marckux.stockman.stock.application.dtos.CreateLocationRequest;
import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.dtos.UpdateLocationRequest;

/**
 * Tests de integración para Locations.
 */
public class LocationIntegrationTest extends IntegrationBaseTest {

  /**
   * Verifica CRUD completo de Locations con SUPER_ADMIN.
   */
  @Test
  @DisplayName("CRUD de locations por SUPER_ADMIN")
  void shouldHandleCrudForSuperAdmin() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");

    CenterResponse center = superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro A"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(CenterResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(center).isNotNull();

    LocationResponse created = superAdminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Almacén", "Zona A", null))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(LocationResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(created).isNotNull();
    String locationId = created.id();

    superAdminClient.get()
      .uri("/api/stock/locations/{id}", locationId)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.name").isEqualTo("Almacén");

    superAdminClient.get()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$[0].name").exists();

    superAdminClient.put()
      .uri("/api/stock/locations/{id}", locationId)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new UpdateLocationRequest("Nevera", "Zona B"))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.name").isEqualTo("Nevera")
      .jsonPath("$.isActive").isEqualTo(true);

    superAdminClient.delete()
      .uri("/api/stock/locations/{id}", locationId)
      .exchange()
      .expectStatus().isNoContent();

    superAdminClient.get()
      .uri("/api/stock/locations/{id}", locationId)
      .exchange()
      .expectStatus().isNotFound();

    LocationResponse recreated = superAdminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Nevera", "Zona B", null))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(LocationResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(recreated).isNotNull();
  }

  /**
   * Verifica que READ requiere autenticación.
   */
  @Test
  @DisplayName("READ requiere autenticación para locations")
  void shouldRequireAuthForRead() {
    webTestClient.get()
      .uri("/api/stock/locations/{id}", java.util.UUID.randomUUID())
      .exchange()
      .expectStatus().isForbidden();
  }

  /**
   * Verifica que READ permite USER autenticado.
   */
  @Test
  @DisplayName("READ permite usuario autenticado")
  void shouldAllowReadForAuthenticatedUser() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");

    CenterResponse center = superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro B"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(CenterResponse.class)
      .returnResult()
      .getResponseBody();

    superAdminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Mochila", null, null))
      .exchange()
      .expectStatus().isCreated();

    var authClient = webTestClientWithUser("user", "USER");

    authClient.get()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .exchange()
      .expectStatus().isOk();
  }

  /**
   * Verifica rechazo por nombre duplicado en el mismo Center.
   */
  @Test
  @DisplayName("No permite duplicar nombre de location en el mismo center")
  void shouldRejectDuplicateNameInSameCenter() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");

    CenterResponse center = superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro C"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(CenterResponse.class)
      .returnResult()
      .getResponseBody();

    superAdminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Almacén", null, null))
      .exchange()
      .expectStatus().isCreated();

    superAdminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Almacén", null, null))
      .exchange()
      .expectStatus().isBadRequest();
  }

  /**
   * Verifica permisos de escritura para ADMIN.
   */
  @Test
  @DisplayName("ADMIN puede crear, actualizar y eliminar locations")
  void shouldAllowWriteForAdmin() {
    var superAdminClient = webTestClientWithUser("super_admin@example.mail", "SUPER_ADMIN");
    CenterResponse center = superAdminClient.post()
      .uri("/api/stock/centers")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateCenterRequest("Centro Admin"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(CenterResponse.class)
      .returnResult()
      .getResponseBody();

    var adminClient = webTestClientWithUser("super_admin@example.mail", "ADMIN");

    LocationResponse created = adminClient.post()
      .uri("/api/stock/centers/{centerId}/locations", center.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new CreateLocationRequest("Locker", null, null))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(LocationResponse.class)
      .returnResult()
      .getResponseBody();

    adminClient.put()
      .uri("/api/stock/locations/{id}", created.id())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new UpdateLocationRequest("Locker 2", null))
      .exchange()
      .expectStatus().isOk();

    adminClient.delete()
      .uri("/api/stock/locations/{id}", created.id())
      .exchange()
      .expectStatus().isNoContent();
  }
}
