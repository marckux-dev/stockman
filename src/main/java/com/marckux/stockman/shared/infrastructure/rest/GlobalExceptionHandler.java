package com.marckux.stockman.shared.infrastructure.rest;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.marckux.stockman.auth.domain.exceptions.DomainException;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.shared.infrastructure.rest.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UnsupportedOperationException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedOperationException (UnsupportedOperationException ex) {
    return buildResponse(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
  }

  @ExceptionHandler(InvalidAttributeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidAttributeException(DomainException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales no válidas");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getDefaultMessage())
      .collect(Collectors.joining("; "));
    return buildResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    String message = "Conflicto de integridad de base de datos.";
    if (ex.getRootCause() != null)
      message = ex.getRootCause().getMessage();
    return buildResponse(HttpStatus.CONFLICT, message);
  }

  @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
public ResponseEntity<ErrorResponse> handleExpiredJwtException(io.jsonwebtoken.ExpiredJwtException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "El token ha expirado. Por favor, inicie sesión de nuevo.");
}

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException (ResourceNotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
    var errorResponse = new ErrorResponse(status.value(), message, System.currentTimeMillis());
    return new ResponseEntity<ErrorResponse>(errorResponse, status);
  }

}
