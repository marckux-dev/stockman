package com.marckux.stockman.shared.domain.exceptions;

public class ResourceNotFoundException extends DomainException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String resourceName, String identifier) {
    this(String.format("%s con el identificador %s no encontrado", resourceName, identifier));
  }
 
  
}
