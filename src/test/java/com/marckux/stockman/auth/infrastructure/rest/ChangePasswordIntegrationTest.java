package com.marckux.stockman.auth.infrastructure.rest;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.marckux.stockman.auth.application.dtos.ChangePasswordRequest;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordIntegrationTest extends IntegrationBaseTest {

  @Test
  @DisplayName("Integración: Usuario puede cambiar su password y autenticarse con la nueva")
  void shouldChangePasswordAndLoginWithNewPassword() {
    String adminToken = login("super_admin@example.mail", "super");

    String unique = UUID.randomUUID().toString().replace("-", "");
    String email = "change_pass_" + unique + "@example.mail";
    String oldPassword = "OldPass1";
    String newPassword = "NewPass2";

    RegisterRequest newUserReq = new RegisterRequest(email, oldPassword, "Change Pass User");
    webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(newUserReq)
      .exchange()
      .expectStatus().isCreated();

    String userToken = login(email, oldPassword);

    ChangePasswordRequest changeReq = new ChangePasswordRequest(oldPassword, newPassword);
    webTestClient.post()
      .uri("/api/auth/change-password")
      .header("Authorization", "Bearer " + userToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(changeReq)
      .exchange()
      .expectStatus().isNoContent();

    webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest(email, oldPassword))
      .exchange()
      .expectStatus().isUnauthorized()
      .expectBody()
      .jsonPath("$.message").isEqualTo("Credenciales no válidas");

    LoginResponse loginResponse = webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest(email, newPassword))
      .exchange()
      .expectStatus().isOk()
      .expectBody(LoginResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(loginResponse).isNotNull();
    assertThat(loginResponse.token()).isNotBlank();
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
