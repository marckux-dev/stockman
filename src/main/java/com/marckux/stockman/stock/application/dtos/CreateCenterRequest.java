package com.marckux.stockman.stock.application.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear un Center.
 */
public record CreateCenterRequest(
    @NotBlank(message = "El nombre del centro es obligatorio")
    String name
) {
}
