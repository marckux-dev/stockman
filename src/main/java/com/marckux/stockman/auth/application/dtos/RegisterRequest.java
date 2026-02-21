package com.marckux.stockman.auth.application.dtos;

import com.marckux.stockman.auth.application.validation.UniqueEmail;
import com.marckux.stockman.shared.domain.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

    @NotBlank(message = "El email es obligatorio")
    @Pattern(
      regexp = ValidationConstants.EMAIL_REGEX,
      message = "El formato del email no es válido"
    )
    @UniqueEmail
    String email
) {
  public RegisterRequest {
    if (email != null)
      email = email.toLowerCase().trim();
  }
}
