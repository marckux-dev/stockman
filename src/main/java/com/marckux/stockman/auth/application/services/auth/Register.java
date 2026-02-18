package com.marckux.stockman.auth.application.services.auth;

import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.RegisterUseCase;
import com.marckux.stockman.auth.application.ports.out.PasswordHasherPort;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Register implements RegisterUseCase {

  private final PasswordHasherPort passwordHasher;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public UserResponse execute(RegisterRequest request) {
    Email email = Email.of(request.email());
    HashedPassword hashedPassword = HashedPassword.of(passwordHasher.encode(request.password()));
    String name = request.name();
    User user = User.builder()
      .email(email)
      .hashedPassword(hashedPassword)
      .name(name)
      .build();
    User savedUser = userRepository.save(user);
    return UserResponse.fromDomain(savedUser);
  }
  
}
