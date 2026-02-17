package com.marckux.stockman.auth.infrastructure.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.FindAllUsersUseCase;
import com.marckux.stockman.auth.application.ports.in.LoginUseCase;
import com.marckux.stockman.auth.application.ports.in.RegisterUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final LoginUseCase loginUseCase;
  private final RegisterUseCase registerUseCase;
  private final FindAllUsersUseCase findAllUsersUseCase;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(loginUseCase.login(request));
  }

  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
    UserResponse userResponse = registerUseCase.register(request);
    URI location = ServletUriComponentsBuilder
      .fromCurrentContextPath()
      .path("/api/auth/users/{id}")
      .buildAndExpand(userResponse.id())
      .toUri();
    return ResponseEntity.created(location).body(userResponse);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<UserResponse>> findAll() {
    return ResponseEntity.ok(findAllUsersUseCase.findAll());
  }
  
}
