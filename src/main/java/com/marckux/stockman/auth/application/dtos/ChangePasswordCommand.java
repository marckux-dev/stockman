package com.marckux.stockman.auth.application.dtos;

public record ChangePasswordCommand(
    String email,
    String currentPassword,
    String newPassword
) {}
