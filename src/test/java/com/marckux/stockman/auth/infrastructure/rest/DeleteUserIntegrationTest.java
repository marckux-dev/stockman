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
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.infrastructure.persistence.JpaUserRepository;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

public class DeleteUserIntegrationTest extends IntegrationBaseTest {

  @Autowired
  private JpaUserRepository jpaUserRepository;

  @Test
  @DisplayName("Integración: SUPER_ADMIN puede eliminar un usuario normal")
  void shouldDeleteUserWhenSuperAdmin() {
    String adminToken = login("super_admin@example.mail", "super");
    UserResponse createdUser = registerUser(adminToken, "delete_user_");
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
  @DisplayName("Integración: ADMIN puede eliminar USER pero no ADMIN")
  void shouldAllowAdminToDeleteUserButNotAdmin() {
    String superAdminToken = login("super_admin@example.mail", "super");

    UserResponse adminCandidate = registerUser(superAdminToken, "admin_candidate_");
    String adminEmail = adminCandidate.email();
    String activationToken = jpaUserRepository.findByEmail(adminEmail).orElseThrow().getToken();

    webTestClient.patch()
      .uri("/api/auth/change-password-with-token")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new ChangePasswordWithTokenRequest(activationToken, "AdminPass1"))
      .exchange()
      .expectStatus().isNoContent();

    webTestClient.patch()
      .uri("/api/auth/users/" + adminCandidate.id() + "/promote-admin")
      .header("Authorization", "Bearer " + superAdminToken)
      .exchange()
      .expectStatus().isOk();

    String adminToken = login(adminEmail, "AdminPass1");
    UserResponse victim = registerUser(superAdminToken, "victim_user_");

    webTestClient.delete()
      .uri("/api/auth/users/" + victim.id())
      .header("Authorization", "Bearer " + adminToken)
      .exchange()
      .expectStatus().isNoContent();

    UserResponse otherAdmin = registerUser(superAdminToken, "other_admin_");
    String otherAdminEmail = otherAdmin.email();
    String otherAdminToken = jpaUserRepository.findByEmail(otherAdminEmail).orElseThrow().getToken();

    webTestClient.patch()
      .uri("/api/auth/change-password-with-token")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new ChangePasswordWithTokenRequest(otherAdminToken, "AdminPass2"))
      .exchange()
      .expectStatus().isNoContent();

    webTestClient.patch()
      .uri("/api/auth/users/" + otherAdmin.id() + "/promote-admin")
      .header("Authorization", "Bearer " + superAdminToken)
      .exchange()
      .expectStatus().isOk();

    webTestClient.delete()
      .uri("/api/auth/users/" + otherAdmin.id())
      .header("Authorization", "Bearer " + adminToken)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.message").isEqualTo("Un ADMIN solo puede eliminar usuarios USER");
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

  private UserResponse registerUser(String adminToken, String prefix) {
    String unique = UUID.randomUUID().toString().replace("-", "");
    String email = prefix + unique + "@example.mail";

    return webTestClient.post()
      .uri("/api/auth/register")
      .header("Authorization", "Bearer " + adminToken)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(new RegisterRequest(email))
      .exchange()
      .expectStatus().isCreated()
      .expectBody(UserResponse.class)
      .returnResult()
      .getResponseBody();
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
