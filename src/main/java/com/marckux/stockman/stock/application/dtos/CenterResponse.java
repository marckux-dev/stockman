package com.marckux.stockman.stock.application.dtos;

import java.time.Instant;

import com.marckux.stockman.stock.domain.model.Center;

/**
 * DTO de salida para un Center.
 */
public record CenterResponse(
    String id,
    String name,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) {
  /**
   * Mapea un Center de dominio a su DTO de respuesta.
   */
  public static CenterResponse fromDomain(Center center) {
    return new CenterResponse(
        center.getId() != null ? center.getId().toString() : null,
        center.getName(),
        center.isActive(),
        center.getCreatedAt(),
        center.getUpdatedAt(),
        center.getCreatedBy() != null ? center.getCreatedBy().toString() : null,
        center.getUpdatedBy() != null ? center.getUpdatedBy().toString() : null
    );
  }
}
