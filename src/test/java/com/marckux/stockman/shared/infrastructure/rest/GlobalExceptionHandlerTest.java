package com.marckux.stockman.shared.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.marckux.stockman.shared.domain.exceptions.DomainException;
import com.marckux.stockman.shared.BaseTest;
import com.marckux.stockman.shared.infrastructure.rest.dto.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;

public class GlobalExceptionHandlerTest extends BaseTest {

  private GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleInvalidAttributeException_ShouldReturnBadRequest() {
    // GIVEN
    var ex = new DomainException("Error") {};
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleInvalidAttributeException(ex);
    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void handleBadCredentialsException_ShouldReturnUnauthorized() {
    // GIVEN
    var ex = new BadCredentialsException("Credenciales inválidas");
    // WHEN
    ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(ex);
    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Debería retornar 400 cuando falla la validación de argumentos")
  void handleMethodArgumentNotValidException_ShoulReturnBadRequest () throws Exception {
    // GIVEN
    var error1 = new FieldError("object", "field", "Message Error 1");
    var error2 = new FieldError("object2", "field", "Message Error 2");
    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
    bindingResult.addError(error1);
    bindingResult.addError(error2);
    MethodParameter methodParameter = new MethodParameter(
        GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
    var ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
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
    var ex = new DataIntegrityViolationException("Integrity violation");
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

  private void dummyMethod(String value) {
    // Solo para obtener MethodParameter en tests.
  }
  
}
