package com.marckux.stockman.auth.infrastructure.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marckux.stockman.auth.application.dtos.ChangePasswordCommand;
import com.marckux.stockman.auth.application.dtos.ChangePasswordRequest;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.DeleteUserByIdUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.FindAllUsersUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.FindUserByIdUsecase;
import com.marckux.stockman.auth.application.ports.in.usecases.LoginUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.RegisterUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final LoginUseCase login;
  private final RegisterUseCase register;
  private final ChangePasswordUseCase changePassword;
  private final DeleteUserByIdUseCase deleteUserById;
  private final FindAllUsersUseCase findAllUsers;
  private final FindUserByIdUsecase findUserById;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(login.execute(request));
  }

  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
    UserResponse userResponse = register.execute(request);
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
    return ResponseEntity.ok(findAllUsers.execute(null));
  }

  @GetMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> findById(@PathVariable("id") String id) {
    log.info("El valor del String Id es " + id);
    return ResponseEntity.ok(findUserById.execute(UUID.fromString(id)));
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
    deleteUserById.execute(UUID.fromString(id));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      @AuthenticationPrincipal UserDetails user,
      @RequestBody @Valid ChangePasswordRequest request) {
    ChangePasswordCommand command = new ChangePasswordCommand(
      user.getUsername(),
      request.currentPassword(),
      request.newPassword()
    );
    changePassword.execute(command);
    return ResponseEntity.noContent().build();
  }
  
  
}
