package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Expiration extends BaseEntity {

  private final LocalDate date;
  private final Product product;

  @Builder(toBuilder = true)
  public Expiration(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      LocalDate date,
      Product product) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    this.date = date;
    this.product = product;
  }
}
