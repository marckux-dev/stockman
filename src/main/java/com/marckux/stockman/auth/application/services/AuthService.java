package com.marckux.stockman.auth.application.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.AuthResponse;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.ports.LoginUseCase;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.TokenProviderPort;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

  private final AuthenticationManager authenticationManager;
  private final TokenProviderPort tokenProvider;
  private final UserRepositoryPort userRepository;

  @Override
  public AuthResponse login(LoginRequest request) {
    final String email = request.email();
    final String password = request.password();
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password));
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticación"));
    String token = tokenProvider.generateToken(user);
    return new AuthResponse(token);
  }

}
