package com.marckux.stockman.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User extends BaseEntity {

  Email email;
  HashedPassword hashedPassword;
  Role role;
  ActivationStatus activationStatus;
  String token;
  Instant tokenExpiration;


  @Builder(toBuilder = true)
  public User(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      Email email,
      HashedPassword hashedPassword,
      Role role,
      ActivationStatus activationStatus,
      String token,
      Instant tokenExpiration) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    if (email == null)
      throw new InvalidAttributeException("El email no puede estar vacío");

    this.email = email;
    this.hashedPassword = hashedPassword;
    this.role = (role != null) ? role : Role.USER;
    this.activationStatus = (activationStatus != null) ? activationStatus : ActivationStatus.INACTIVE;
    this.token = token;
    this.tokenExpiration = tokenExpiration;

    if (isActivated() && this.hashedPassword == null) {
      throw new InvalidAttributeException("Un usuario activo requiere password");
    }
    if ((this.token == null) != (this.tokenExpiration == null)) {
      throw new InvalidAttributeException("Token y expiración deben venir juntos");
    }
  }

  public boolean isActivated() {
    return this.activationStatus == ActivationStatus.ACTIVE;
  }

  public boolean isBlocked() {
    return this.activationStatus == ActivationStatus.BLOCKED;
  }

  public boolean hasValidToken(String providedToken, Instant now) {
    if (providedToken == null || token == null || tokenExpiration == null)
      return false;
    return token.equals(providedToken) && tokenExpiration.isAfter(now);
  }

  public User assignPasswordAndActivate(HashedPassword newHashedPassword) {
    if (newHashedPassword == null)
      throw new InvalidAttributeException("La password no puede estar vacía");
    return this.toBuilder()
      .hashedPassword(newHashedPassword)
      .activationStatus(ActivationStatus.ACTIVE)
      .token(null)
      .tokenExpiration(null)
      .build();
  }

  public User issueToken(String newToken, Instant expiration) {
    if (newToken == null || newToken.isBlank())
      throw new InvalidAttributeException("El token no puede estar vacío");
    if (expiration == null)
      throw new InvalidAttributeException("La expiración del token es obligatoria");
    return this.toBuilder()
      .token(newToken)
      .tokenExpiration(expiration)
      .build();
  }

  public User upgradeRole(Role targetRole) {
    if (targetRole == null)
      throw new InvalidAttributeException("El role destino es obligatorio");
    return this.toBuilder()
      .role(targetRole)
      .build();
  }

  public User block() {
    return this.toBuilder()
      .activationStatus(ActivationStatus.BLOCKED)
      .token(null)
      .tokenExpiration(null)
      .build();
  }

}
