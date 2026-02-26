package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de dominio que representa un centro.
 */
@Getter
public class Center extends BaseEntity {

  private final String name;

  /**
   * Crea un Center validando sus atributos obligatorios.
   */
  @Builder(toBuilder = true)
  public Center(
      UUID id,
      String name,
      Boolean isActive,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    validateName(name);
    this.name = name;
  }

  public Center rename(String newName) {
    validateName(newName);
    return this.toBuilder()
      .name(newName)
      .build();
  }

  public Center changeActiveStatus(boolean newStatus) {
    return this.toBuilder()
      .isActive(newStatus)
      .build();
  }

  /**
   * Valida el nombre del centro.
   */
  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new InvalidAttributeException("El nombre del centro es obligatorio");
    }
  }

}
