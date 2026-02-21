package com.marckux.stockman.auth.application.services.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.out.SecureTokenGeneratorPort;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.AccountNotificationPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class RegisterTest extends BaseTest {

  @Mock
  private UserRepositoryPort userRepository;

  @Mock
  private SecureTokenGeneratorPort secureTokenGenerator;

  @Mock
  private AccountNotificationPort accountNotificationPort;

  @InjectMocks
  private Register register;

  @Test
  void shouldRegisterANewUserSuccessfully() {
    String email = "new-user@example.mail";
    String token = "secure-token";
    User savedUser = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .role(Role.USER)
      .activationStatus(ActivationStatus.INACTIVE)
      .token(token)
      .tokenExpiration(Instant.now().plusSeconds(1800))
      .build();

    ReflectionTestUtils.setField(register, "tokenExpirationMinutes", 30);
    when(secureTokenGenerator.generate()).thenReturn(token);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    UserResponse userResponse = register.execute(new RegisterRequest(email));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    verify(accountNotificationPort).sendActivationToken(savedUser, token);
    User userSent = userCaptor.getValue();

    assertEquals(Role.USER, userSent.getRole());
    assertEquals(ActivationStatus.INACTIVE, userSent.getActivationStatus());
    assertEquals(token, userSent.getToken());
    assertNotNull(userSent.getTokenExpiration());
    assertNotNull(userResponse);
    assertEquals(email, userResponse.email());
    assertEquals(Role.USER.name(), userResponse.role());
    assertEquals(ActivationStatus.INACTIVE.name(), userResponse.activationStatus());
  }
}
