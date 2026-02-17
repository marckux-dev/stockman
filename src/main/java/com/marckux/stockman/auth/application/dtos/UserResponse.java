package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.domain.model.User;

public record UserResponse(
    String id,
    String email,
    String name,
    String role
) {
  public static UserResponse fromDomain(User user) {
    return new UserResponse(
        user.getId().toString(),
        user.getEmail().getValue(),
        user.getName(),
        user.getRole().name()
    );
  }
}
