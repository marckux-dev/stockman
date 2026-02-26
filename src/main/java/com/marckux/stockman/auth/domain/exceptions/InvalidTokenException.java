package com.marckux.stockman.auth.domain.exceptions;

import com.marckux.stockman.shared.domain.exceptions.DomainException;

public class InvalidTokenException extends DomainException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
