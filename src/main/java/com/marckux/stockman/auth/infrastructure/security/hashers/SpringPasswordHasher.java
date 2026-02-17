package com.marckux.stockman.auth.infrastructure.security.hashers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringPasswordHasher implements PasswordHasherPort {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String password) {
    return passwordEncoder.encode(password);
  }



  
}
