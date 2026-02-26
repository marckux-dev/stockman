package com.marckux.stockman.auth.application.dtos;

public record ChangePasswordWithTokenCommand(
    String token,
    String newPassword
) {}
