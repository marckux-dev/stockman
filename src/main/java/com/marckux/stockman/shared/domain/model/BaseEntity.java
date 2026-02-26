package com.marckux.stockman.shared.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class BaseEntity {

  protected final UUID id;
  protected final Instant createdAt;
  protected final Instant updatedAt;
  protected final UUID createdBy;
  protected final UUID updatedBy;
  protected final boolean isActive;

  protected BaseEntity(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      boolean isActive) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    this.isActive = isActive;
  }
}
