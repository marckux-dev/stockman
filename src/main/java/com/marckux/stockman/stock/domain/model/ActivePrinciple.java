package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ActivePrinciple extends BaseEntity {

  private final String name;
  private final String synonims;
  private final String searchVector;
  private final TherapeuticGroup therapeuticGroup;
  private final Set<Product> products;

  @Builder(toBuilder = true)
  public ActivePrinciple(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy,
      Boolean isActive,
      String name,
      String synonims,
      String searchVector,
      TherapeuticGroup therapeuticGroup,
      Set<Product> products) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    this.name = name;
    this.synonims = synonims;
    this.searchVector = searchVector;
    this.therapeuticGroup = therapeuticGroup;
    this.products = products != null ? products : Collections.emptySet();
  }
}
