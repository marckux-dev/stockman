package com.marckux.stockman.stock.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.model.BaseEntity;

import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de dominio que representa una ubicación (Location) dentro de un Center.
 */
@Getter
public class Location extends BaseEntity {

  private final String name;
  private final String description;
  private final Center center;
  private final Location parentLocation;

  /**
   * Crea una Location validando sus atributos obligatorios.
   */
  @Builder(toBuilder = true)
  public Location(
      UUID id,
      String name,
      String description,
      Center center,
      Location parentLocation,
      Boolean isActive,
      Instant createdAt,
      Instant updatedAt,
      UUID createdBy,
      UUID updatedBy) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, isActive != null ? isActive : true);
    validateName(name);
    validateCenter(center);
    validateNoCycle(id, parentLocation);
    this.name = name;
    this.description = description;
    this.center = center;
    this.parentLocation = parentLocation;
  }


  /**
   * Renombra la Location.
   */
  public Location rename(String newName) {
    validateName(newName);
    return this.toBuilder()
      .name(newName)
      .build();
  }

  /**
   * Cambia la descripción.
   */
  public Location changeDescription(String newDescription) {
    return this.toBuilder()
      .description(newDescription)
      .build();
  }

  /**
   * Cambia el estado activo.
   */
  public Location changeActiveStatus(boolean newStatus) {
    return this.toBuilder()
      .isActive(newStatus)
      .build();
  }

  /**
   * Cambia la localización padre
   */
  public Location changeParentLocation(Location newLocation) {
    validateNoCycle(this.getId(), newLocation);
    return this.toBuilder()
      .parentLocation(newLocation)
      .build();
  }

  /**
   * Valida el nombre.
   */
  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new InvalidAttributeException("El nombre de la ubicación es obligatorio");
    }
  }

  /**
   * Valida el Center asociado.
   */
  private static void validateCenter(Center center) {
    if (center == null) {
      throw new InvalidAttributeException("El centro es obligatorio");
    }
  }

  /**
   * Evita ciclos en la jerarquía de ubicaciones.
   */
  private static void validateNoCycle(UUID locationId, Location parentLocation) {
    if (parentLocation == null) {
      return;
    }
    if (locationId != null && locationId.equals(parentLocation.getId())) {
      throw new InvalidAttributeException("La ubicación padre no puede ser la misma ubicación");
    }
    Location current = parentLocation;
    while (current != null) {
      if (locationId != null && locationId.equals(current.getId())) {
        throw new InvalidAttributeException("La ubicación padre genera un ciclo");
      }
      current = current.getParentLocation();
    }
  }
}
