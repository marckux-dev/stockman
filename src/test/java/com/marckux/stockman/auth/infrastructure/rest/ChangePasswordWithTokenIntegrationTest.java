package com.marckux.stockman.auth.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.marckux.stockman.auth.application.dtos.ChangePasswordWithTokenRequest;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.infrastructure.persistence.JpaUserRepository;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

public class ChangePasswordWithTokenIntegrationTest extends IntegrationBaseTest {

  @Autowired
  private JpaUserRepository jpaUserRepository;

  @Test
  @DisplayName("Integración: Usuario configura password con token y luego puede hacer login")
  void shouldSetPasswordWithTokenAndLogin() {
    String adminToken = login("super_admin@example.mail", "super");

    String unique = UUID.randomUUID().toString().replace("-", "");
    String email = "change_pass_" + unique + "@example.mail";
    String newPassword = "NewPass2";

    webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest(email))
      .exchange()
      .expectStatus().isCreated();

    String token = jpaUserRepository.findByEmail(email)
      .orElseThrow()
      .getToken();
    assertThat(token).isNotBlank();

    ChangePasswordWithTokenRequest changeReq = new ChangePasswordWithTokenRequest(token, newPassword);
    webTestClient.patch()
      .uri("/api/auth/change-password-with-token")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(changeReq)
      .exchange()
      .expectStatus().isNoContent();

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
