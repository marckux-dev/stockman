package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de Centers.
 */
public interface JpaCenterRepository extends JpaRepository<CenterEntity, UUID> {

  /**
   * Busca por nombre.
   */
  Optional<CenterEntity> findByName(String name);

  /**
   * Verifica existencia por nombre.
   */
  boolean existsByName(String name);
}
