package com.marckux.stockman.shared.infrastructure.persistence.auditing;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.marckux.stockman.auth.infrastructure.security.models.AuthUserDetails;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AuditingEntityListener {

  @PrePersist
  public void prePersist(Object target) {
    if (!(target instanceof AuditableJpaEntity auditable)) {
      return;
    }
    Instant now = Instant.now();
    if (auditable.getCreatedAt() == null) {
      auditable.setCreatedAt(now);
    }
    if (auditable.getCreatedBy() == null) {
      auditable.setCreatedBy(resolveCurrentUserId());
    }
  }

  @PreUpdate
  public void preUpdate(Object target) {
    if (!(target instanceof AuditableJpaEntity auditable)) {
      return;
    }
    Instant now = Instant.now();
    auditable.setUpdatedAt(now);
    UUID currentUserId = resolveCurrentUserId();
    if (currentUserId != null) {
      auditable.setUpdatedBy(currentUserId);
    }
  }

  private UUID resolveCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthUserDetails details) {
      return details.getId();
    }
    if (principal instanceof UserDetails details) {
      String username = details.getUsername();
      if (username == null) {
        return null;
      }
      try {
        return UUID.fromString(username);
      } catch (IllegalArgumentException ex) {
        return null;
      }
    }
    return null;
  }
}
