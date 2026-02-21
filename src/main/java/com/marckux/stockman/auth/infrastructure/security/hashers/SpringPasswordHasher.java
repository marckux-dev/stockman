package com.marckux.stockman.auth.infrastructure.security.hashers;

import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringPasswordHasher implements PasswordHasherPort {

  private static final Pattern BCRYPT_HASH_PATTERN = Pattern
      .compile("^\\$2([aby]|x)?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String password) {
    return passwordEncoder.encode(password);
  }

  @Override
  public boolean matches(String rawPassword, String hashedPassword) {
    return passwordEncoder.matches(rawPassword, hashedPassword);
  }

  @Override
  public boolean isHashed(String password) {
    return password != null && BCRYPT_HASH_PATTERN.matcher(password).matches();
  }

}
