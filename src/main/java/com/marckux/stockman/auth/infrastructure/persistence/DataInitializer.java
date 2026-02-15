package com.marckux.stockman.auth.infrastructure.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.model.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final JpaUserRepository repository;
  private final PasswordEncoder encoder;

  @Override
  public void run(String... args) throws Exception {
    if (repository.count() == 0) {
      UserEntity superAdmin = UserEntity.builder()
          .email("super_admin@example.mail")
          .name("Super Admin")
          .password(encoder.encode("super"))
          .role(Role.SUPER_ADMIN.name())
          .isActive(true)
          .build();
      repository.save(superAdmin);
      System.out.println("🚀 Usuario Super Admin creado; " +
          "email: super_admin@example.mail " +
          "password: super");
    }
  }
}
