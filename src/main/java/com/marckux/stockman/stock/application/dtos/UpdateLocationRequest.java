package com.marckux.stockman.stock.application.dtos;

/**
 * DTO para actualizar una Location.
 */
public record UpdateLocationRequest(
    String name,
    String description
) {
}
