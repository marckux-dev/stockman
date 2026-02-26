package com.marckux.stockman.shared.infrastructure;

import com.marckux.stockman.shared.BaseTest;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public abstract class IntegrationBaseTest extends BaseTest {

    @Autowired
    private WebApplicationContext applicationContext;

    protected WebTestClient webTestClient;

    @BeforeEach
    void setUpClient() {
        this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(applicationContext)
            .apply(springSecurity())
            .configureClient()
            .responseTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * Crea un WebTestClient con usuario autenticado y CSRF por defecto.
     *
     * @param username nombre del usuario autenticado.
     * @param roles roles asignados al usuario.
     * @return cliente configurado con autenticación y CSRF.
     */
    protected WebTestClient webTestClientWithUser(String username, String... roles) {
        RequestBuilder defaultRequest = MockMvcRequestBuilders.get("/")
            .with(user(username).roles(roles))
            .with(csrf());
        return MockMvcWebTestClient.bindToApplicationContext(applicationContext)
            .apply(springSecurity())
            .defaultRequest(defaultRequest)
            .configureClient()
            .responseTimeout(Duration.ofSeconds(30))
            .build();
    }
}
