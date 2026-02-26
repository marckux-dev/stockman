package com.marckux.stockman.stock.application.dtos;

import java.time.Instant;

import com.marckux.stockman.stock.domain.model.Location;

/**
 * DTO de salida para una Location.
 */
public record LocationResponse(
    String id,
    String centerId,
    String name,
    String description,
    String parentLocation,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) {
  /**
   * Mapea una Location de dominio a su DTO de respuesta.
   */
  public static LocationResponse fromDomain(Location location) {
    return new LocationResponse(
      location.getId() != null ? location.getId().toString() : null,
      location.getCenter().getId() != null ? location.getCenter().getId().toString() : null,
      location.getName(),
      location.getDescription(),
      location.getParentLocation() != null && location.getParentLocation().getId() != null
        ? location.getParentLocation().getId().toString()
        : null,
      location.isActive(),
      location.getCreatedAt(),
      location.getUpdatedAt(),
      location.getCreatedBy() != null ? location.getCreatedBy().toString() : null,
      location.getUpdatedBy() != null ? location.getUpdatedBy().toString() : null
    );
  }
}
