package com.marckux.stockman.auth.application.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.ports.out.IdentityManagerPort;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.TokenProviderPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest extends BaseTest {

  @Mock
  private IdentityManagerPort identityManager;

  @Mock
  private TokenProviderPort tokenProvider;

  @Mock
  private UserRepositoryPort userRepository;

  @InjectMocks
  private LoginService loginService;

  @Test
  @DisplayName("Debería hacer login correctamente y devolver un token")
  void shouldLoginSuccessfully() {
    // GIVEN
    String email = "email@example.mail";
    String password = "password";
    String hashedPassword = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";
    String token = "token";
    LoginRequest request = new LoginRequest(email, password);
    User user = User.builder()
        .email(Email.of(email))
        .hashedPassword(HashedPassword.of(hashedPassword))
        .role(Role.ADMIN)
        .isActive(true)
        .build();

    doNothing().when(identityManager).authenticate(anyString(), anyString());
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(tokenProvider.generateToken(user)).thenReturn(token);

    // WHEN
    LoginResponse response = loginService.login(request);

    // THEN
    assertNotNull(response);
    assertEquals(response.token(), token);

  }

}
