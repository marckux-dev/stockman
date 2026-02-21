package com.marckux.stockman.auth.application.dtos;

public record ChangePasswordCommand(
    String token,
    String newPassword
) {}
