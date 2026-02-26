package com.marckux.stockman.auth.infrastructure.security.mappers;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.infrastructure.security.models.AuthUserDetails;

@Component
public class SecurityUserMapper {

  public UserDetails toUserDetails(com.marckux.stockman.auth.domain.model.User user) {
    String password = user.getHashedPassword() != null
      ? user.getHashedPassword().getValue()
      : "{noop}__NO_PASSWORD__";
    return new AuthUserDetails(
      user.getId(),
      user.getEmail().getValue(),
      password,
      List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
      user.isActive() && user.isActivated(),
      true,
      !user.isBlocked(),
      true
    );
  }
  
}
