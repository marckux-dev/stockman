package com.marckux.stockman.auth.infrastructure.security.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.marckux.stockman.auth.infrastructure.security.mappers.SecurityUserMapper;
import com.marckux.stockman.shared.BaseTest;

import io.jsonwebtoken.lang.Collections;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest extends BaseTest {

  @InjectMocks
  private JwtService jwtService;
  @Mock
  private SecurityUserMapper mapper;

  private final String SECRET = "La_clave_esta_Rebecca_es_una_obra_de_Ken_Follet";

  @BeforeEach
  void setUp() {
    // Inyectamos manualmente los valores que normalmente vienen del properties
    ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
  }

  @Test
  @DisplayName("Debería lanzar ExpiredJwtException si el token ha sido manipulado")
  void shouldThrowExceptionWhenTokenIsExpired() {
    // GIVEN: Expiración negativa para que caduque al instante
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -3600000);

    UserDetails user = org.springframework.security.core.userdetails.User.builder()
        .username("test@mail.com")
        .password("pass")

        .authorities(Collections.emptyList())
        .build();
    String token = jwtService.generateToken(user);

    // WHEN & THEN: Al intentar validar o extraer, debe lanzar la excepción
    assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
      jwtService.isTokenValid(token, user);
    });
  }

}
