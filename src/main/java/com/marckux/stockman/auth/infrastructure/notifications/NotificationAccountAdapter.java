package com.marckux.stockman.auth.infrastructure.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.AccountNotificationPort;
import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;
import com.marckux.stockman.notification.application.ports.in.usecases.SendRichEmailUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAccountAdapter implements AccountNotificationPort {

  @Value("${app.frontend.base-url:http://localhost:8080}")
  private String frontendBaseUrl;

  @Value("${app.notification.email-enabled:true}")
  private boolean emailEnabled;

  private final SendRichEmailUseCase sendRichEmail;

  @Override
  public void sendActivationToken(User user, String token) {
    if (!emailEnabled)
      return;
    String link = "%s/configure-password?token=%s".formatted(frontendBaseUrl, token);
    String message = """
        Hemos creado tu cuenta en Stockman.
        Para definir tu contraseña usa este enlace:
        %s
        """.formatted(link);
    try {
      sendRichEmail.execute(new SendRichEmailRequest(
        user.getEmail().getValue(),
        "Usuario",
        "Configura tu contraseña",
        message
      ));
    } catch (RuntimeException ex) {
      // TODO(PROD): restaurar el error para fallar la operacion si el email es obligatorio.
      log.warn("No se pudo enviar el email de activacion para el usuario {}", user.getEmail().getValue(), ex);
    }
  }

  @Override
  public void sendPasswordResetToken(User user, String token) {
    if (!emailEnabled)
      return;
    String link = "%s/reset-password?token=%s".formatted(frontendBaseUrl, token);
    String message = """
        Hemos recibido una solicitud para resetear tu contraseña.
        Para continuar usa este enlace:
        %s
        """.formatted(link);
    try {
      sendRichEmail.execute(new SendRichEmailRequest(
        user.getEmail().getValue(),
        "Usuario",
        "Reseteo de contraseña",
        message
      ));
    } catch (RuntimeException ex) {
      // TODO(PROD): restaurar el error para fallar la operacion si el email es obligatorio.
      log.warn("No se pudo enviar el email de reseteo de contraseña para el usuario {}", user.getEmail().getValue(), ex);
    }
  }
}
