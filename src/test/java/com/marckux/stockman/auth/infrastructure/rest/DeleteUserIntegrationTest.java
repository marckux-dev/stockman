package com.marckux.stockman.auth.infrastructure.rest;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteUserIntegrationTest extends IntegrationBaseTest {

  @Test
  @DisplayName("Integración: SUPER_ADMIN puede eliminar un usuario normal")
  void shouldDeleteUserWhenSuperAdmin() {
    String adminToken = login("super_admin@example.mail", "super");

    String unique = UUID.randomUUID().toString().replace("-", "");
    String email = "delete_user_" + unique + "@example.mail";
    RegisterRequest newUserReq = new RegisterRequest(email, "StrongPass1!", "Delete User");

    UserResponse createdUser = webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(newUserReq)
      .exchange()
      .expectStatus().isCreated()
      .expectBody(UserResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(createdUser).isNotNull();

    webTestClient.delete()
      .uri("/api/auth/users/" + createdUser.id())
      .header("Authorization", "Bearer " + adminToken)
      .exchange()
      .expectStatus().isNoContent();

    webTestClient.get()
      .uri("/api/auth/users/" + createdUser.id())
      .header("Authorization", "Bearer " + adminToken)
      .exchange()
      .expectStatus().isNotFound();
  }

  @Test
  @DisplayName("Integración: Un usuario normal no puede eliminar usuarios")
  void shouldForbidDeleteWhenUserRoleIsNotSuperAdmin() {
    String adminToken = login("super_admin@example.mail", "super");

    String unique = UUID.randomUUID().toString().replace("-", "");
    String userEmail = "normal_user_" + unique + "@example.mail";
    RegisterRequest normalUserReq = new RegisterRequest(userEmail, "StrongPass1!", "Normal User");

    UserResponse createdUser = webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(normalUserReq)
      .exchange()
      .expectStatus().isCreated()
      .expectBody(UserResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(createdUser).isNotNull();

    String userToken = login(userEmail, "StrongPass1!");

    webTestClient.delete()
      .uri("/api/auth/users/" + createdUser.id())
      .header("Authorization", "Bearer " + userToken)
      .exchange()
      .expectStatus().isForbidden();
  }

  @Test
  @DisplayName("Integración: No se puede eliminar a un SUPER_ADMIN")
  void shouldRejectDeletingSuperAdmin() {
    String adminToken = login("super_admin@example.mail", "super");
    LoginResponse adminLogin = webTestClient.post()
      .uri("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new LoginRequest("super_admin@example.mail", "super"))
      .exchange()
      .expectStatus().isOk()
      .expectBody(LoginResponse.class)
      .returnResult()
      .getResponseBody();

    assertThat(adminLogin).isNotNull();
    String adminId = adminLogin.user().id();

    webTestClient.delete()
      .uri("/api/auth/users/" + adminId)
      .header("Authorization", "Bearer " + adminToken)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.message").isEqualTo("No se puede eliminar un SUPER_ADMIN");
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
