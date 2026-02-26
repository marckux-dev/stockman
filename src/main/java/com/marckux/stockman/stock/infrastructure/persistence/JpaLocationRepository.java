package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de Locations.
 */
public interface JpaLocationRepository extends JpaRepository<LocationEntity, UUID> {

  /**
   * Busca por nombre y center.
   */
  Optional<LocationEntity> findByCenterIdAndName(UUID centerId, String name);

  /**
   * Verifica existencia por nombre y center.
   */
  boolean existsByCenterIdAndName(UUID centerId, String name);

  /**
   * Lista locations por center.
   */
  List<LocationEntity> findAllByCenterId(UUID centerId);
}
