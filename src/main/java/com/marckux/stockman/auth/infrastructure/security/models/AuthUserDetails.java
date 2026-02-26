package com.marckux.stockman.auth.infrastructure.security.models;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUserDetails implements UserDetails {

  private final UUID id;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;

  public AuthUserDetails(
      UUID id,
      String email,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      boolean enabled,
      boolean accountNonExpired,
      boolean accountNonLocked,
      boolean credentialsNonExpired) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
