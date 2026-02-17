package com.marckux.stockman.shared.infrastructure.rest.dto;

public record ErrorResponse(
    int status,
    String message,
    long timestamp) {
}
