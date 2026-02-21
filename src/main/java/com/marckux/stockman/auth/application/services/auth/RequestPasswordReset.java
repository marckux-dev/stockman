package com.marckux.stockman.auth.application.services.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.RequestPasswordResetRequest;
import com.marckux.stockman.auth.application.ports.in.usecases.RequestPasswordResetUseCase;
import com.marckux.stockman.auth.application.ports.out.SecureTokenGeneratorPort;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.AccountNotificationPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestPasswordReset implements RequestPasswordResetUseCase {

  @Value("${app.auth.token-expiration-minutes:30}")
  private int tokenExpirationMinutes;

  private final UserRepositoryPort userRepository;
  private final SecureTokenGeneratorPort secureTokenGenerator;
  private final AccountNotificationPort accountNotification;

  @Override
  @Transactional
  public Void execute(RequestPasswordResetRequest request) {
    String email = Email.of(request.email()).getValue();
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null)
      return null;
    if (user.isBlocked())
      return null;

    String token = secureTokenGenerator.generate();
    Instant expiration = Instant.now().plus(tokenExpirationMinutes, ChronoUnit.MINUTES);
    User updated = user.issueToken(token, expiration);
    userRepository.save(updated);
    accountNotification.sendPasswordResetToken(updated, token);
    return null;
  }
}
