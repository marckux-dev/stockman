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
import com.marckux.stockman.auth.application.dtos.BlockUserCommand;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RequestPasswordResetRequest;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.BlockUserUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.DeleteUserByIdUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.FindAllUsersUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.FindUserByIdUsecase;
import com.marckux.stockman.auth.application.ports.in.usecases.LoginUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.PromoteUserToAdminUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.PromoteUserToSuperAdminUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.RequestPasswordResetUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.RegisterUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para autenticación y administración de usuarios.
 */
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
  private final PromoteUserToAdminUseCase promoteUserToAdmin;
  private final PromoteUserToSuperAdminUseCase promoteUserToSuperAdmin;
  private final BlockUserUseCase blockUser;
  private final RequestPasswordResetUseCase requestPasswordReset;

  /**
   * Autentica un usuario y retorna el token JWT.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(login.execute(request));
  }

  /**
   * Crea un usuario inactivo y retorna su representación.
   */
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

  /**
   * Lista todos los usuarios.
   */
  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<UserResponse>> findAll() {
    return ResponseEntity.ok(findAllUsers.execute(null));
  }

  /**
   * Busca un usuario por id.
   */
  @GetMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> findById(@PathVariable("id") String id) {
    return ResponseEntity.ok(findUserById.execute(UUID.fromString(id)));
  }

  /**
   * Elimina un usuario por id.
   */
  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
    deleteUserById.execute(UUID.fromString(id));
    return ResponseEntity.noContent().build();
  }

  /**
   * Promueve un usuario a ADMIN.
   */
  @PatchMapping("/users/{id}/promote-admin")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable("id") String id) {
    return ResponseEntity.ok(promoteUserToAdmin.execute(UUID.fromString(id)));
  }

  /**
   * Promueve un usuario a SUPER_ADMIN.
   */
  @PatchMapping("/users/{id}/promote-super-admin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<UserResponse> promoteToSuperAdmin(@PathVariable("id") String id) {
    return ResponseEntity.ok(promoteUserToSuperAdmin.execute(UUID.fromString(id)));
  }

  /**
   * Bloquea un usuario con auditoría del solicitante.
   */
  @PatchMapping("/users/{id}/block")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> blockUser(
      @PathVariable("id") String id,
      @AuthenticationPrincipal UserDetails requester) {
    BlockUserCommand command = new BlockUserCommand(UUID.fromString(id), requester.getUsername());
    return ResponseEntity.ok(blockUser.execute(command));
  }

  /**
   * Cambia la password mediante token de un solo uso.
   */
  @PatchMapping("/change-password")
  public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    ChangePasswordCommand command = new ChangePasswordCommand(
      request.token(),
      request.newPassword()
    );
    changePassword.execute(command);
    return ResponseEntity.noContent().build();
  }
  
  /**
   * Solicita un email de reseteo de password.
   */
  @PostMapping("/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(
      @RequestBody @Valid RequestPasswordResetRequest request) {
    requestPasswordReset.execute(request);
    return ResponseEntity.noContent().build();
  }
  
  
}
