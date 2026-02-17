package com.marckux.stockman.auth.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "Introduce un email")
    String email,

    @NotBlank(message = "Introduce una password")
    String password) {
}
