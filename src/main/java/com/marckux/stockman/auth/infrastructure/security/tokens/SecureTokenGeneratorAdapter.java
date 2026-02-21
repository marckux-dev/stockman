package com.marckux.stockman.auth.infrastructure.security.tokens;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.application.ports.out.SecureTokenGeneratorPort;

@Component
public class SecureTokenGeneratorAdapter implements SecureTokenGeneratorPort {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @Override
  public String generate() {
    byte[] bytes = new byte[32];
    SECURE_RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
