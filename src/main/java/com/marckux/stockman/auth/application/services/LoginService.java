package com.marckux.stockman.auth.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.ports.in.LoginUseCase;
import com.marckux.stockman.auth.application.ports.out.IdentityManagerPort;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.TokenProviderPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService implements LoginUseCase {

  private final IdentityManagerPort identityManager;
  private final TokenProviderPort tokenProvider;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    final String email = request.email();
    final String password = request.password();
    Email emailVo = Email.of(email);
    identityManager.authenticate(emailVo.getValue(), password);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticación"));
    String token = tokenProvider.generateToken(user);
    return new LoginResponse(token);
  }

}
