package com.marckux.stockman.auth.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

public class AuthSecurityIntegrationTest extends IntegrationBaseTest {

  @Test
  @DisplayName("Seguridad: sin token no se puede registrar usuarios")
  void shouldDenyRegistrationWithoutToken() {
    webTestClient.post().uri("/api/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest("pepe@stockman.com"))
      .exchange()
      .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("Seguridad: Login con password incorrecta debe devolver 401")
  void shouldReturnUnauthorizedWithWrongPassword() {
    LoginRequest badLogin = new LoginRequest("super_admin@example.mail", "WRONG_PASSWORD");

    webTestClient.post().uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(badLogin)
      .exchange()
      .expectStatus().isUnauthorized()
      .expectBody()
      .jsonPath("$.message").isEqualTo("Credenciales no válidas");
  }

  @Test
  @DisplayName("Seguridad: Acceso a ruta privada sin token debe devolver 403/401")
  void shouldDenyAccessWithoutToken() {
    webTestClient.get().uri("/api/private/check-health")
      .exchange()
      .expectStatus().isForbidden();
  }

  @ParameterizedTest(name = "Validación: Email inválido ''{0}'' debe ser rechazado (400)")
  @ValueSource(strings = {"short", "invalid", "@mail.com", ""})
  void shouldRejectInvalidEmailInRegister(String invalidEmail) {
    String adminToken = login("super_admin@example.mail", "super");

    webTestClient.post().uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest(invalidEmail))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.message").value(msg ->
        assertThat(msg.toString()).containsAnyOf(
          "El formato del email no es válido",
          "El email es obligatorio"
        )
      );
  }

  private String login(String email, String password) {
    return webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest(email, password))
      .exchange()
      .expectStatus().isOk()
      .expectBody(LoginResponse.class)
      .returnResult()
      .getResponseBody()
      .token();
  }
}
