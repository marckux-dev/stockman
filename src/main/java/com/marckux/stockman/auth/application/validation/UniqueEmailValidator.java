package com.marckux.stockman.auth.application.validation;

import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

  private final UserRepositoryPort userRepository;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null || email.isBlank()) {
      return true; // Dejamos que @NotBlank maneje los nulos
    }
    return userRepository.findByEmail(email).isEmpty();

  }
}
