package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.domain.model.User;

import java.time.Instant;

public record UserResponse(
    String id,
    String email,
    String role,
    String activationStatus,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) {
  public static UserResponse fromDomain(User user) {
    return new UserResponse(
        user.getId().toString(),
        user.getEmail().getValue(),
        user.getRole().name(),
        user.getActivationStatus().name(),
        user.isActive(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getCreatedBy() != null ? user.getCreatedBy().toString() : null,
        user.getUpdatedBy() != null ? user.getUpdatedBy().toString() : null
    );
  }
}
