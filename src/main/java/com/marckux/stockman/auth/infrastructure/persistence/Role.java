package com.marckux.stockman.auth.infrastructure.persistence;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
  SUPER_ADMIN,
  ADMIN,
  USER;

  public List<SimpleGrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
  }
}
