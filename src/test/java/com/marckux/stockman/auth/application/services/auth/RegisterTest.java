package com.marckux.stockman.auth.application.services.auth;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
public class RegisterTest extends BaseTest {

  private static String HASHED = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

  @Mock
  private UserRepositoryPort userRepository;

  @Mock
  private PasswordHasherPort passwordHasher;

  @InjectMocks
  private Register register;

  @Test
  void shouldRegisterANewUserSuccessfully() {
    // GIVEN
    String email = "new-user@example.mail";
    String password = "Abcd1234";
    String name = "New User";
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of(email))
      .hashedPassword(HashedPassword.of(HASHED))
      .name(name)
      .role(Role.USER)
      .build();
    RegisterRequest request = new RegisterRequest(email, password, name);
    when(passwordHasher.encode(password)).thenReturn(HASHED);
    when(userRepository.save(any(User.class))).thenReturn(user);
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    // WHEN
    UserResponse userResponse = register.execute(request);
    // THEN
    verify(userRepository).save(userCaptor.capture());
    User userSent = userCaptor.getValue();
    assertNotEquals(password, userSent.getHashedPassword().getValue());
    assertEquals(HASHED, userSent.getHashedPassword().getValue());
    assertNotNull(userResponse);
    assertEquals(email, userResponse.email());
    assertEquals(name, userResponse.name());
    assertEquals(Role.USER.name(), userResponse.role());
  }
}
