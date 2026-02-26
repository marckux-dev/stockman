package com.marckux.stockman.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.shared.infrastructure.persistence.auditing.AuditableJpaEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserEntity extends AuditableJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = true)
  private String password;

  @Column(nullable = false)
  private String role;

  @Enumerated(EnumType.STRING)
  @Column(name = "activation_status", nullable = false)
  @Builder.Default
  private ActivationStatus activationStatus = ActivationStatus.INACTIVE;

  @Column(name = "token")
  private String token;

  @Column(name = "token_expiration")
  private Instant tokenExpiration;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

}
