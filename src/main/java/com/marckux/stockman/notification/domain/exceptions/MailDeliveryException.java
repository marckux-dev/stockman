package com.marckux.stockman.notification.domain.exceptions;

public class MailDeliveryException extends RuntimeException {

  public MailDeliveryException(String message, Throwable cause) {
    super(message, cause);
  }
}
