package com.marckux.stockman.auth.application.services.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.RegisterUseCase;
import com.marckux.stockman.auth.application.ports.out.SecureTokenGeneratorPort;
import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.AccountNotificationPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Register implements RegisterUseCase {

  @Value("${app.auth.token-expiration-minutes:30}")
  private int tokenExpirationMinutes;

  private final SecureTokenGeneratorPort secureTokenGenerator;
  private final AccountNotificationPort accountNotification;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public UserResponse execute(RegisterRequest request) {
    Email email = Email.of(request.email());
    String token = secureTokenGenerator.generate();
    Instant expiration = Instant.now().plus(tokenExpirationMinutes, ChronoUnit.MINUTES);

    User user = User.builder()
      .email(email)
      .role(Role.USER)
      .activationStatus(ActivationStatus.INACTIVE)
      .token(token)
      .tokenExpiration(expiration)
      .build();

    User savedUser = userRepository.save(user);
    accountNotification.sendActivationToken(savedUser, token);
    return UserResponse.fromDomain(savedUser);
  }
  
}
