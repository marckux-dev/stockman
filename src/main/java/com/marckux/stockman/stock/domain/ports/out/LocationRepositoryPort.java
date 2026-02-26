package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Location;

/**
 * Puerto de persistencia para Locations.
 */
public interface LocationRepositoryPort {

  /**
   * Guarda una Location.
   */
  Location save(Location location);

  /**
   * Busca por id.
   */
  Optional<Location> findById(UUID id);

  /**
   * Busca una Location por id incluyendo inactivas.
   */
  Optional<Location> findByIdIncludingInactive(UUID id);

  /**
   * Busca por nombre y center.
   */
  Optional<Location> findByCenterIdAndName(UUID centerId, String name);

  /**
   * Verifica existencia por nombre y center.
   */
  boolean existsByCenterIdAndName(UUID centerId, String name);

  /**
   * Lista locations por center.
   */
  List<Location> findAllByCenterId(UUID centerId);

  /**
   * Elimina por id.
   */
  void deleteById(UUID id);
}
