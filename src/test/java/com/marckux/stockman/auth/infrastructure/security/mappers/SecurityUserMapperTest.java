package com.marckux.stockman.auth.infrastructure.security.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.shared.BaseTest;

public class SecurityUserMapperTest extends BaseTest {

  private static final String VALID_HASH = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQiy38a";
  private final SecurityUserMapper mapper = new SecurityUserMapper();

  @Test
  void shouldMapDomainUserToUserDetails() {
    // GIVEN
    User user = User.builder()
        .id(UUID.randomUUID())
        .email(Email.of("text@example.mail"))
        .hashedPassword(HashedPassword.of(VALID_HASH))
        .role(Role.USER)
        .activationStatus(ActivationStatus.ACTIVE)
        .build();
    // WHEN
    UserDetails userDetails = mapper.toUserDetails(user);
    // THEN
    assertEquals(user.getEmail().getValue(), userDetails.getUsername());
    assertEquals(user.getHashedPassword().getValue(), userDetails.getPassword());
    assertTrue(userDetails.isEnabled());
    assertTrue(userDetails.getAuthorities().stream().anyMatch(
        a -> a.getAuthority().equals("ROLE_USER")));

  }

  @Test
  void shouldMapBlockedUserAsLocked() {
    User user = User.builder()
      .id(UUID.randomUUID())
      .email(Email.of("blocked@example.mail"))
      .hashedPassword(HashedPassword.of(VALID_HASH))
      .role(Role.USER)
      .activationStatus(ActivationStatus.BLOCKED)
      .build();

    UserDetails userDetails = mapper.toUserDetails(user);
    assertTrue(userDetails.isAccountNonLocked() == false);
  }

}
