package com.marckux.stockman.auth.infrastructure.notifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.notification.application.ports.in.usecases.SendRichEmailUseCase;
import com.marckux.stockman.notification.domain.exceptions.MailDeliveryException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class NotificationAccountAdapterTest {

  @Mock
  private SendRichEmailUseCase sendRichEmail;

  @Test
  void shouldLogWarningAndNotThrowWhenEmailFails() {
    NotificationAccountAdapter adapter = new NotificationAccountAdapter(sendRichEmail);
    ReflectionTestUtils.setField(adapter, "emailEnabled", true);
    ReflectionTestUtils.setField(adapter, "frontendBaseUrl", "http://localhost:8080");

    User user = User.builder()
      .email(Email.of("fail@stockman.com"))
      .build();

    Logger logger = (Logger) LoggerFactory.getLogger(NotificationAccountAdapter.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);

    when(sendRichEmail.execute(any())).thenThrow(
      new MailDeliveryException("No se pudo enviar", new RuntimeException("smtp"))
    );

    assertDoesNotThrow(() -> adapter.sendActivationToken(user, "token"));

    List<ILoggingEvent> logs = appender.list;
    assertThat(logs).anySatisfy(event -> {
      assertThat(event.getLevel()).isEqualTo(Level.WARN);
      assertThat(event.getFormattedMessage()).contains("email de activacion");
      assertThat(event.getFormattedMessage()).contains("fail@stockman.com");
    });

    logger.detachAppender(appender);
  }
}
