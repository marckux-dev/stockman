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

import com.marckux.stockman.auth.application.dtos.ChangePasswordWithTokenCommand;
import com.marckux.stockman.auth.application.dtos.ChangePasswordWithTokenRequest;
import com.marckux.stockman.auth.application.dtos.ChangePasswordWithPasswordCommand;
import com.marckux.stockman.auth.application.dtos.ChangePasswordWithPasswordRequest;
import com.marckux.stockman.auth.application.dtos.BlockUserCommand;
import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;
import com.marckux.stockman.auth.application.dtos.RequestPasswordResetRequest;
import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.BlockUserUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordWithPasswordUseCase;
import com.marckux.stockman.auth.application.ports.in.usecases.ChangePasswordWithTokenUseCase;
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
  private final ChangePasswordWithTokenUseCase changePasswordWithToken;
  private final ChangePasswordWithPasswordUseCase changePasswordWithPassword;
  private final DeleteUserByIdUseCase deleteById;
  private final FindAllUsersUseCase findAll;
  private final FindUserByIdUsecase findUserById;
  private final PromoteUserToAdminUseCase promoteToAdmin;
  private final PromoteUserToSuperAdminUseCase promoteToSuperAdmin;
  private final BlockUserUseCase block;
  private final RequestPasswordResetUseCase requestPasswordReset;

  /**
   * Autentica un usuario y retorna el token JWT.
   *
   * @param request credenciales del usuario.
   * @return token JWT.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(login.execute(request));
  }

  /**
   * Crea un usuario inactivo y retorna su representación.
   *
   * @param request datos del nuevo usuario.
   * @return representación del usuario creado.
   */
  @PostMapping("/register")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterRequest request) {
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
   *
   * @return lista de usuarios.
   */
  @GetMapping("/users")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<List<UserResponse>> findAllUsers() {
    return ResponseEntity.ok(findAll.execute(null));
  }

  /**
   * Busca un usuario por id.
   *
   * @param id identificador del usuario.
   * @return usuario encontrado.
   */
  @GetMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> findUserById(@PathVariable("id") String id) {
    return ResponseEntity.ok(findUserById.execute(UUID.fromString(id)));
  }

  /**
   * Elimina un usuario por id.
   *
   * @param id identificador del usuario.
   * @return respuesta sin contenido.
   */
  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<Void> deleteUserById(
      @PathVariable("id") String id,
      @AuthenticationPrincipal UserDetails requester) {
    deleteById.execute(new DeleteUserByIdUseCase.Input(UUID.fromString(id), requester.getUsername()));
    return ResponseEntity.noContent().build();
  }

  /**
   * Promueve un usuario a ADMIN.
   *
   * @param id identificador del usuario.
   * @return usuario actualizado.
   */
  @PatchMapping("/users/{id}/promote-admin")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> promoteUserToAdmin(@PathVariable("id") String id) {
    return ResponseEntity.ok(promoteToAdmin.execute(UUID.fromString(id)));
  }

  /**
   * Promueve un usuario a SUPER_ADMIN.
   *
   * @param id identificador del usuario.
   * @return usuario actualizado.
   */
  @PatchMapping("/users/{id}/promote-super-admin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<UserResponse> promoteUserToSuperAdmin(@PathVariable("id") String id) {
    return ResponseEntity.ok(promoteToSuperAdmin.execute(UUID.fromString(id)));
  }

  /**
   * Bloquea un usuario con auditoría del solicitante.
   *
   * @param id identificador del usuario.
   * @param requester usuario solicitante.
   * @return usuario actualizado.
   */
  @PatchMapping("/users/{id}/block")
  @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
  public ResponseEntity<UserResponse> blockUser(
      @PathVariable("id") String id,
      @AuthenticationPrincipal UserDetails requester) {
    BlockUserCommand command = new BlockUserCommand(UUID.fromString(id), requester.getUsername());
    return ResponseEntity.ok(block.execute(command));
  }

  /**
   * Cambia la password de un usuario autenticado si se proporciona la password anterior
   *
   * @param request datos de la password anterior y nueva.
   * @param requested usuario autenticado solicitante.
   * @return respuesta sin contenido.
   */
  @PatchMapping("/change-password-with-password")
  public ResponseEntity<Void> changeUserPasswordWithPassword(
      @RequestBody @Valid ChangePasswordWithPasswordRequest request,
      @AuthenticationPrincipal UserDetails requested) {
    ChangePasswordWithPasswordCommand command = new ChangePasswordWithPasswordCommand(requested.getUsername(),
        request.oldPassword(), request.newPassword());
    changePasswordWithPassword.execute(command);
    return ResponseEntity.noContent().build();
  }

  /**
   * Cambia la password mediante token de un solo uso.
   *
   * @param request token y nueva password.
   * @return respuesta sin contenido.
   */
  @PatchMapping("/change-password-with-token")
  public ResponseEntity<Void> changeUserPasswordWithToken(@RequestBody @Valid ChangePasswordWithTokenRequest request) {
    var command = new ChangePasswordWithTokenCommand(
        request.token(),
        request.newPassword());
    changePasswordWithToken.execute(command);
    return ResponseEntity.noContent().build();
  }

  /**
   * Solicita un email de reseteo de password.
   *
   * @param request datos de la solicitud.
   * @return respuesta sin contenido.
   */
  @PostMapping("/request-password-reset")
  public ResponseEntity<Void> requestUserPasswordReset(
      @RequestBody @Valid RequestPasswordResetRequest request) {
    requestPasswordReset.execute(request);
    return ResponseEntity.noContent().build();
  }

}
