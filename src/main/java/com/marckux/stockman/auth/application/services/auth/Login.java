package com.marckux.stockman.auth.application.services.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.LoginUseCase;
import com.marckux.stockman.auth.application.ports.out.IdentityManagerPort;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.TokenProviderPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Login implements LoginUseCase {

  private final IdentityManagerPort identityManager;
  private final TokenProviderPort tokenProvider;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional(readOnly = true)
  public LoginResponse execute(LoginRequest request) {
    final String email = Email.of(request.email()).getValue();
    final String password = request.password();
    identityManager.authenticate(email, password);
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrad tras autenticación exitosa?"));
    String token = tokenProvider.generateToken(user);
    return new LoginResponse(token, UserResponse.fromDomain(user));
  }

}
