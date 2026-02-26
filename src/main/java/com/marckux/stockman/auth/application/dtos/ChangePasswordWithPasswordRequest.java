package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.application.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordWithPasswordRequest(

    @NotBlank(message = "La password antigua es obligatoria")
    String oldPassword,

    @NotBlank(message = "La nueva password es obligatoria")
    @StrongPassword
    String newPassword
) {}

