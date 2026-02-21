package com.marckux.stockman.auth.domain.exceptions;

public class InvalidTokenException extends DomainException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
