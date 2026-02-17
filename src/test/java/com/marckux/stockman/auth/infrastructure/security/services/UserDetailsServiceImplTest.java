package com.marckux.stockman.auth.infrastructure.security.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.auth.infrastructure.security.mappers.SecurityUserMapper;
import com.marckux.stockman.shared.BaseTest;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest extends BaseTest {

  private static String HASHED = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";

  @Mock
  private UserRepositoryPort userRepository;

  @Mock
  private SecurityUserMapper mapper;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  @Test
  @DisplayName("Debería cargar un usuario por su email correctamente")
  void shouldLoadUserByUsernameSuccessfully() {
    // GIVEN
    String email = "pro@stockman.com";
    User user = User.builder()
        .id(UUID.randomUUID())
        .email(Email.of(email))
        .hashedPassword(HashedPassword.of(HASHED))
        .role(Role.USER)
        .name("Pro")
        .build();
    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
        .username(email)
        .password(HASHED)
        .authorities(Collections.emptyList())
        .build();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(mapper.toUserDetails(any(User.class))).thenReturn(userDetails);
    // WHEN
    UserDetails result = userDetailsService.loadUserByUsername(email);

    // THEN
    assertNotNull(result);
  }

  @Test
  @DisplayName("Debería lanzar una excepción si no encuentra el email")
  void shouldThrowExceptionIfUserNotFound() {
    String email = "pro@stockman.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    assertThrows(UsernameNotFoundException.class, 
      () -> {userDetailsService.loadUserByUsername(email);});

  }
}
