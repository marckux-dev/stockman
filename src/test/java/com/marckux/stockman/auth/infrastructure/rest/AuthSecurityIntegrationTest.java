package com.marckux.stockman.auth.infrastructure.rest;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.shared.infrastructure.IntegrationBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSecurityIntegrationTest extends IntegrationBaseTest {

    @Test
    @DisplayName("Seguridad: Un usuario normal (USER) NO debe poder registrar nuevos usuarios (403 Forbidden)")
    void shouldDenyRegistrationToNonAdminUsers() {
        // 1. Necesitamos un token de ADMIN primero para crear al usuario normal
        String adminToken = login("super_admin@example.mail", "super");

        // 2. Creamos un usuario normal ("Pepe")
        RegisterRequest normalUserReq = new RegisterRequest(
                "pepe@stockman.com", "PepePass123!", "Pepe User"
        );
        
        webTestClient.post().uri("/api/auth/register")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(normalUserReq)
                .exchange()
                .expectStatus().isCreated();

        // 3. Nos logueamos como "Pepe" (Rol USER por defecto)
        String userToken = login("pepe@stockman.com", "PepePass123!");

        // 4. Pepe intenta registrar a otro usuario (Acción prohibida)
        RegisterRequest intruderReq = new RegisterRequest(
                "hacker@stockman.com", "HackerPass1!", "Hacker"
        );

        webTestClient.post().uri("/api/auth/register")
                .header("Authorization", "Bearer " + userToken) // Token de usuario normal
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(intruderReq)
                .exchange()
                .expectStatus().isForbidden() // Esperamos 403
                .expectBody()
                // Spring Security por defecto a veces no devuelve cuerpo en 403, 
                // o devuelve un error genérico. Validamos solo el status.
                ;
    }

    @Test
    @DisplayName("Seguridad: Login con password incorrecta debe devolver 401")
    void shouldReturnUnauthorizedWithWrongPassword() {
        LoginRequest badLogin = new LoginRequest("super_admin@example.mail", "WRONG_PASSWORD");

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badLogin)
                .exchange()
                .expectStatus().isUnauthorized() // 401
                .expectBody()
                .jsonPath("$.message").isEqualTo("Credenciales no válidas");
    }

    @Test
    @DisplayName("Seguridad: Acceso a ruta privada sin token debe devolver 403/401")
    void shouldDenyAccessWithoutToken() {
        webTestClient.get().uri("/api/private/check-health")
                .exchange()
                // Dependiendo de tu configuración de SecurityFilterChain, 
                // sin token suele ser 403 (Forbidden) o 401 (Unauthorized).
                // En JWT Filter suele saltar 403 si no hay contexto de seguridad.
                .expectStatus().isForbidden(); 
    }

    @ParameterizedTest(name = "Validación: Password débil ''{0}'' debe ser rechazada (400)")
    @ValueSource(strings = {
            "short",       // Muy corta
            "onlyletters", // Sin números ni mayúsculas
            "12345678",    // Solo números
            "NoNumbers!",  // Sin números
            ""             // Vacía
    })
    void shouldRejectWeakPasswords(String weakPassword) {
        // Token de admin para intentar el registro
        String adminToken = login("super_admin@example.mail", "super");

        RegisterRequest weakReq = new RegisterRequest(
                "weak@test.com", 
                weakPassword, 
                "Weak User"
        );

        webTestClient.post().uri("/api/auth/register")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(weakReq)
                .exchange()
                .expectStatus().isBadRequest() // 400
                .expectBody()
                // Verificamos que el mensaje de error mencione algo de validación
                // (Tu GlobalExceptionHandler concatena los errores)
                .jsonPath("$.message").value(msg -> 
                    assertThat(msg.toString()).containsAnyOf(
                        "La password debe tener", 
                        "La password es obligatoria"
                    )
                );
    }

    // --- HELPER PARA LOGIN RÁPIDO ---
    // Este método encapsula la lógica de obtener el token para no repetir código
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
