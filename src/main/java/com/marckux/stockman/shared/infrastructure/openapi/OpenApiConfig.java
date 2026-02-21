package com.marckux.stockman.shared.infrastructure.openapi;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

/**
 * Centraliza la configuración base de OpenAPI/Swagger.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Stockman API",
        version = "v1",
        description = "API de autenticación y notificaciones para Stockman."
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}
