package com.marckux.stockman.auth.infrastructure.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.AccountNotificationPort;
import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;
import com.marckux.stockman.notification.application.ports.in.usecases.SendRichEmailUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
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
    sendRichEmail.execute(new SendRichEmailRequest(
      user.getEmail().getValue(),
      "Usuario",
      "Configura tu contraseña",
      message
    ));
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
    sendRichEmail.execute(new SendRichEmailRequest(
      user.getEmail().getValue(),
      "Usuario",
      "Reseteo de contraseña",
      message
    ));
  }
}
