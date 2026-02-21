package com.marckux.stockman.auth.application.services.auth;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.ChangePasswordCommand;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordUseCase;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.InvalidTokenException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePassword implements ChangePasswordUseCase {

  private final PasswordHasherPort passwordHasher;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public Void execute(ChangePasswordCommand command) {
    String token = command.token();
    User user = userRepository.findByToken(token)
      .orElseThrow(() -> new ResourceNotFoundException("Token", token));
    if (user.isBlocked()) {
      throw new InvalidAttributeException("Usuario bloqueado");
    }
    if (!user.hasValidToken(token, Instant.now())) {
      throw new InvalidTokenException("Token inválido o expirado");
    }

    HashedPassword hashedPassword = HashedPassword.of(passwordHasher.encode(command.newPassword()));
    User updatedUser = user.assignPasswordAndActivate(hashedPassword);
    userRepository.save(updatedUser);
    return null;
  }
}
