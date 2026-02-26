package com.marckux.stockman.auth.application.dtos;

public record ChangePasswordWithPasswordCommand(
    String email,
    String oldPassword,
    String newPassword
) {}

