package com.marckux.stockman.auth.infrastructure.security.identitymanagers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.application.ports.out.IdentityManagerPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringSecurityIdentityManager implements IdentityManagerPort {

  private final AuthenticationManager authenticationManager;

  @Override
  public void authenticate(String email, String password) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(email, password)
    );
  }

  
}
