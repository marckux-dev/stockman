package com.marckux.stockman.auth.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

public class AuthIntegrationTest extends IntegrationBaseTest {

  @Test
  @DisplayName("Integración: Login admin y registro de usuario inactivo")
  void shouldRegisterInactiveUser() {
    LoginResponse adminLoginResponse = webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest("super_admin@example.mail", "super"))
      .exchange()
      .expectStatus().isOk()
      .expectBody(LoginResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(adminLoginResponse).isNotNull();
    String adminToken = adminLoginResponse.token();

    webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest("crypto_trader@stockman.com"))
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.email").isEqualTo("crypto_trader@stockman.com")
      .jsonPath("$.role").isEqualTo("USER")
      .jsonPath("$.activationStatus").isEqualTo("INACTIVE");
  }

  @Test
  @DisplayName("Integración: Debería fallar (400) al registrar email duplicado")
  void shouldFailWhenRegisteringDuplicateEmail() {
    String adminToken = loginAsAdminAndGetToken();

    webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest("super_admin@example.mail"))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.message").value(msg ->
        assertThat(msg.toString()).contains("El email ya está registrado")
      );
  }

  private String loginAsAdminAndGetToken() {
    return webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest("super_admin@example.mail", "super"))
      .exchange()
      .expectStatus().isOk()
      .expectBody(LoginResponse.class)
      .returnResult()
      .getResponseBody()
      .token();
  }
}
