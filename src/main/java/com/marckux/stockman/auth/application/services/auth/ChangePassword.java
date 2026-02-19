package com.marckux.stockman.auth.application.services.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.ChangePasswordCommand;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordUseCase;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
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
    String email = Email.of(command.email()).getValue();
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", email));

    if (!user.getIsActive()) {
      throw new InvalidAttributeException("Usuario inactivo");
    }

    boolean validCurrentPassword = passwordHasher.matches(
      command.currentPassword(),
      user.getHashedPassword().getValue()
    );
    if (!validCurrentPassword) {
      throw new BadCredentialsException("Credenciales no válidas");
    }

    HashedPassword hashedPassword = HashedPassword.of(passwordHasher.encode(command.newPassword()));
    User updatedUser = user.toBuilder().hashedPassword(hashedPassword).build();
    userRepository.save(updatedUser);
    return null;
  }
}
