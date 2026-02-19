package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.application.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

    @NotBlank(message = "La password actual es obligatoria")
    String currentPassword,

    @NotBlank(message = "La nueva password es obligatoria")
    @StrongPassword
    String newPassword
) {}
