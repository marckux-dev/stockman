package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Product extends BaseEntity {

  private final String name;
  private final String shortName;
  private final String synonims;
  private final String searchVector;
  private final Category category;
  private final Set<ActivePrinciple> activePrinciples;
  private final List<Expiration> expirations;

  @Builder(toBuilder = true)
  public Product(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      String name,
      String shortName,
      String synonims,
      String searchVector,
      Category category,
      Set<ActivePrinciple> activePrinciples,
      List<Expiration> expirations) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    this.name = name;
    this.shortName = shortName;
    this.synonims = synonims;
    this.searchVector = searchVector;
    this.category = category;
    this.activePrinciples = activePrinciples != null ? activePrinciples : Collections.emptySet();
    this.expirations = expirations != null ? expirations : Collections.emptyList();
  }
}
