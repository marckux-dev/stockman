package com.marckux.stockman.shared.infrastructure;

import com.marckux.stockman.shared.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort; // Para obtener el puerto real
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationBaseTest extends BaseTest {

    // Spring inyecta aquí el puerto aleatorio que eligió al arrancar
    @LocalServerPort
    protected int port;

    protected WebTestClient webTestClient;

    @BeforeEach
    void setUpClient() {
        // Construimos el cliente moderno manualmente apuntando a nuestro servidor local
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(30)) // Buen hábito para evitar timeouts en tests
                .build();
    }
}
