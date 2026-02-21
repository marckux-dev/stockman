package com.marckux.stockman.notification.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;
import com.marckux.stockman.notification.application.ports.in.usecases.SendRichEmailUseCase;
import com.marckux.stockman.notification.domain.ports.out.EmailSenderPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SendRichEmail implements SendRichEmailUseCase {

  @Value("${app.mail.from}")
  private String from;

  private final EmailSenderPort emailSender;

  @Override
  public Void execute(SendRichEmailRequest request) {
    String safeName = escapeHtml(request.userName());
    String safeMessage = escapeHtml(request.message()).replace("\n", "<br/>");
    String safeSubject = escapeHtml(request.subject());
    String htmlBody = """
        <div style="font-family: Arial, sans-serif; background: #f5f7fb; padding: 24px;">
          <div style="max-width: 640px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e5e7eb;">
            <div style="background: linear-gradient(135deg, #114b8b 0%%, #0b2f57 100%%); padding: 20px;">
              <h1 style="margin: 0; color: #ffffff; font-size: 24px;">Stockman</h1>
              <p style="margin: 6px 0 0; color: #dbeafe; font-size: 14px;">Notificación para usuario</p>
            </div>
            <div style="padding: 24px; color: #111827;">
              <p style="margin: 0 0 12px; font-size: 16px;">Hola <strong>%s</strong>,</p>
              <p style="margin: 0 0 16px; font-size: 15px; line-height: 1.6;">%s</p>
              <div style="margin-top: 20px; padding-top: 16px; border-top: 1px solid #e5e7eb; font-size: 12px; color: #6b7280;">
                Este mensaje fue enviado automáticamente desde Stockman.
              </div>
            </div>
          </div>
          <p style="text-align: center; color: #9ca3af; font-size: 12px; margin-top: 12px;">%s</p>
        </div>
        """.formatted(safeName, safeMessage, safeSubject);

    emailSender.sendHtml(from, request.to(), request.subject(), htmlBody);
    return null;
  }

  private String escapeHtml(String value) {
    if (value == null)
      return "";
    return value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }
}
