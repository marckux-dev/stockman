package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Stock extends BaseEntity {

  private final Integer currentQuantity;
  private final Integer idealQuantity;
  private final Integer threshold;
  private final Product product;
  private final Location location;

  @Builder(toBuilder = true)
  public Stock(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      Integer currentQuantity,
      Integer idealQuantity,
      Integer threshold,
      Product product,
      Location location) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    this.currentQuantity = currentQuantity;
    this.idealQuantity = idealQuantity;
    this.threshold = threshold;
    this.product = product;
    this.location = location;
  }
}
