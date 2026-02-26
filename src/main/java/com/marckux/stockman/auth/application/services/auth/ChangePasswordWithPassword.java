package com.marckux.stockman.auth.application.services.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.ChangePasswordWithPasswordCommand;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordWithPasswordUseCase;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordWithPassword implements ChangePasswordWithPasswordUseCase {

    private final PasswordHasherPort passwordHasher;
    private final UserRepositoryPort userRepository;

  @Override
  public Void execute(ChangePasswordWithPasswordCommand command) {
    String email = command.email();
    String oldPassword = command.oldPassword();
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Usuario con email " + email + " no encontrado"));
    if (!user.isActive() || !user.isActivated())
      throw new BadCredentialsException("No se puede restablecer la password de un usuario inactivo");
    if (!passwordHasher.matches(oldPassword, user.getHashedPassword().getValue()))
      throw new BadCredentialsException("Credenciales no válidas");
    String encodedPassword = passwordHasher.encode(command.newPassword());
    HashedPassword newHashedPassword = HashedPassword.of(encodedPassword);
    User updatedUser = user.assignPasswordAndActivate(newHashedPassword);
    userRepository.save(updatedUser);
    return null;
  }
  
}
