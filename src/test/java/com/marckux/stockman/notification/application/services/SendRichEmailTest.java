package com.marckux.stockman.notification.application.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;
import com.marckux.stockman.notification.domain.ports.out.EmailSenderPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class SendRichEmailTest extends BaseTest {

  @Mock
  private EmailSenderPort emailSender;

  @InjectMocks
  private SendRichEmail sendRichEmail;

  @Test
  @DisplayName("Debería enviar email HTML enriquecido")
  void shouldSendRichHtmlEmail() {
    ReflectionTestUtils.setField(sendRichEmail, "from", "no-reply@stockman.local");
    SendRichEmailRequest request = new SendRichEmailRequest(
      "user@example.com",
      "User <Admin>",
      "Bienvenido",
      "Primera linea\nSegunda linea"
    );

    sendRichEmail.execute(request);

    ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);
    verify(emailSender).sendHtml(
      eq("no-reply@stockman.local"),
      eq("user@example.com"),
      eq("Bienvenido"),
      htmlCaptor.capture()
    );
    assertTrue(htmlCaptor.getValue().contains("Hola <strong>User &lt;Admin&gt;</strong>"));
    assertTrue(htmlCaptor.getValue().contains("Primera linea<br/>Segunda linea"));
  }
}
