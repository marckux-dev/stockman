package com.marckux.stockman.stock.application.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear una Location.
 */
public record CreateLocationRequest(
    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    String name,
    String description,
    String parentLocation
) {
}
