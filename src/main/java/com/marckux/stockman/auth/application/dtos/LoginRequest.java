package com.marckux.stockman.auth.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "Introduce un email")
    @Email(message = "Introduce un email válido")
    String email,

    @NotBlank(message = "Introduce una password")
    String password) {
}
