package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.application.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordWithTokenRequest(
    @NotBlank(message = "El token es obligatorio")
    String token,

    @NotBlank(message = "La nueva password es obligatoria")
    @StrongPassword
    String newPassword
) {}
