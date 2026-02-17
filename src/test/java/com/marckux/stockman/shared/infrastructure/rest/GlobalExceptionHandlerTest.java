package com.marckux.stockman.shared.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.marckux.stockman.auth.domain.exceptions.DomainException;
import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.infrastructure.rest.dto.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;

public class GlobalExceptionHandlerTest extends BaseTest {

  private GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleInvalidAttributeException_ShouldReturnBadRequest() {
    // GIVEN
    var ex = mock(DomainException.class);
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleInvalidAttributeException(ex);
    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void handleBadCredentialsException_ShouldReturnUnauthorized() {
    // GIVEN
    var ex = mock(BadCredentialsException.class);
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(ex);
    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Debería retornar 400 cuando falla la validación de argumentos")
  void handleMethodArgumentNotValidException_ShoulReturnBadRequest () {
    // GIVEN
    var error1 = new FieldError("object", "field", "Message Error 1");
    var error2 = new FieldError("object2", "field", "Message Error 2");
    var ex = mock(MethodArgumentNotValidException.class);
    var bindigResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindigResult);
    when(bindigResult.getFieldErrors()).thenReturn(List.of(error1, error2));
    // WHEN
    var response = handler.handleMethodArgumentNotValidException(ex);
    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().message().contains("Message Error 1"));
    assertTrue(response.getBody().message().contains("Message Error 2"));
  }
  
  @Test
  void handleDataIntegrityViolationException_ShouldReturnConflict () {
    // GIVEN
    var ex = mock(DataIntegrityViolationException.class);
    when(ex.getRootCause()).thenReturn(null);
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(ex);
    // THEN
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  void handleExpiredJwtException_ShouldReturnUnauthorized() {
    // GIVEN
    var ex = new ExpiredJwtException(null, null, "Token expired");
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleExpiredJwtException(ex);
    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
}
