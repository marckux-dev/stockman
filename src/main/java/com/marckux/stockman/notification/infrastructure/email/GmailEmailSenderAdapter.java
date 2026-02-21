package com.marckux.stockman.notification.infrastructure.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.marckux.stockman.notification.domain.exceptions.MailDeliveryException;
import com.marckux.stockman.notification.domain.ports.out.EmailSenderPort;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GmailEmailSenderAdapter implements EmailSenderPort {

  private final JavaMailSender javaMailSender;

  @Override
  public void sendHtml(String from, String to, String subject, String htmlBody) {
    try {
      var mimeMessage = javaMailSender.createMimeMessage();
      var helper = new MimeMessageHelper(mimeMessage, "UTF-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true);
      javaMailSender.send(mimeMessage);
    } catch (MessagingException ex) {
      throw new MailDeliveryException("No fue posible construir el email", ex);
    } catch (RuntimeException ex) {
      throw new MailDeliveryException("No fue posible enviar el email", ex);
    }
  }
}
