package com.marckux.stockman.auth.infrastructure.security.mappers;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserMapper {

  public UserDetails toUserDetails(com.marckux.stockman.auth.domain.model.User user) {
    String password = user.getHashedPassword() != null
      ? user.getHashedPassword().getValue()
      : "{noop}__NO_PASSWORD__";
    return User.builder()
      .username(user.getEmail().getValue())
      .password(password)
      .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
      .disabled(!user.isActive())
      .accountExpired(false)
      .accountLocked(user.isBlocked())
      .credentialsExpired(false)
      .build()
    ;
  }
  
}
