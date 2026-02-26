package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Category extends BaseEntity {

  private final String name;
  private final List<Product> products;

  @Builder(toBuilder = true)
  public Category(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      String name,
      List<Product> products) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    this.name = name;
    this.products = products != null ? products : Collections.emptyList();
  }
}
