package com.marckux.stockman.notification.infrastructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;
import com.marckux.stockman.notification.application.ports.in.usecases.SendRichEmailUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para envío de notificaciones por email.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class EmailController {

  private final SendRichEmailUseCase sendRichEmail;

  /**
   * Envía un email enriquecido.
   */
  @PostMapping("/emails/rich")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> sendRichEmail(@RequestBody @Valid SendRichEmailRequest request) {
    sendRichEmail.execute(request);
    return ResponseEntity.accepted().build();
  }
}
