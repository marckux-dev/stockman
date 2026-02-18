package com.marckux.stockman.auth.infrastructure.rest;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthIntegrationTest extends IntegrationBaseTest {

    @Test
    @DisplayName("Integración: Flujo completo (Login Admin -> Registro Usuario -> Acceso Protegido)")
    void shouldCompleteFullAuthFlow() {
        
        // =================================================================================
        // PASO 1: Login como Super Admin
        // (Este usuario fue creado automáticamente por DataInitializer al levantar el test)
        // =================================================================================
        LoginRequest adminLoginReq = new LoginRequest("super_admin@example.mail", "super");

        LoginResponse adminLoginResponse = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(adminLoginReq)
                .exchange()                 // Ejecuta la petición HTTP real
                .expectStatus().isOk()      // Aserción: Esperamos 200 OK
                .expectBody(LoginResponse.class) // Deserializa el JSON a nuestro DTO Record
                .returnResult()
                .getResponseBody();

        assertThat(adminLoginResponse).isNotNull();
        String adminToken = adminLoginResponse.token();


        // =================================================================================
        // PASO 2: Registrar un nuevo usuario (Requiere rol ADMIN/SUPER_ADMIN)
        // =================================================================================
        RegisterRequest newUserReq = new RegisterRequest(
                "crypto_trader@stockman.com",
                "StrongPass123!",
                "Crypto Trader"
        );

        webTestClient.post()
                .uri("/api/auth/register")
                .header("Authorization", "Bearer " + adminToken) // Inyectamos el token del admin
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUserReq)
                .exchange()
                .expectStatus().isCreated() // Aserción: Esperamos 201 Created
                .expectBody()
                .jsonPath("$.email").isEqualTo("crypto_trader@stockman.com")
                .jsonPath("$.name").isEqualTo("Crypto Trader");


        // =================================================================================
        // PASO 3: Login con el NUEVO usuario
        // =================================================================================
        LoginRequest userLoginReq = new LoginRequest("crypto_trader@stockman.com", "StrongPass123!");

        LoginResponse userLoginResponse = webTestClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userLoginReq)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(userLoginResponse).isNotNull();
        String userToken = userLoginResponse.token();


        // =================================================================================
        // PASO 4: Acceder a ruta protegida con el nuevo token
        // =================================================================================
        webTestClient.get()
                .uri("/api/private/check-health")
                .header("Authorization", "Bearer " + userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").value(msg -> 
                    assertThat(msg.toString()).contains("Hola, crypto_trader@stockman.com")
                );
    }

    @Test
    @DisplayName("Integración: Debería fallar (400) al registrar email duplicado")
    void shouldFailWhenRegisteringDuplicateEmail() {
        
        // 1. Obtener token de Admin (Helper rápido)
        String adminToken = loginAsAdminAndGetToken();

        // 2. Intentar registrar al admin de nuevo (Email ya existe en DB)
        RegisterRequest duplicateReq = new RegisterRequest(
                "super_admin@example.mail", 
                "AnyPass123!", 
                "Duplicated User"
        );

        // 3. Verificar que el GlobalExceptionHandler captura la excepción y devuelve 400
        webTestClient.post()
                .uri("/api/auth/register")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(duplicateReq)
                .exchange()
                .expectStatus().isBadRequest() // Esperamos 400
                .expectBody()
                .jsonPath("$.message").value(msg -> 
                    assertThat(msg.toString()).contains("El email ya está registrado")
                );
    }

    // --- Helper Method para no repetir lógica de login en cada test ---
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
