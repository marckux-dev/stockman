package com.marckux.stockman.notification.application.dtos;

import com.marckux.stockman.shared.domain.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendRichEmailRequest(
    @NotBlank(message = "El email destino es obligatorio")
    @Pattern(
      regexp = ValidationConstants.EMAIL_REGEX,
      message = "El formato del email destino no es válido"
    )
    String to,

    @NotBlank(message = "El nombre del usuario es obligatorio")
    String userName,

    @NotBlank(message = "El asunto es obligatorio")
    String subject,

    @NotBlank(message = "El mensaje es obligatorio")
    String message
) {
  public SendRichEmailRequest {
    if (to != null)
      to = to.toLowerCase().trim();
  }
}
