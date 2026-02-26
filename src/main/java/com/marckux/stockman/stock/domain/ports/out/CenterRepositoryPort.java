package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Center;

/**
 * Puerto de persistencia para Centers.
 */
public interface CenterRepositoryPort {

  /**
   * Guarda un Center.
   */
  Center save(Center center);

  /**
   * Busca un Center por id.
   */
  Optional<Center> findById(UUID id);

  /**
   * Busca un Center por id incluyendo inactivos.
   */
  Optional<Center> findByIdIncludingInactive(UUID id);

  /**
   * Busca un Center por nombre.
   */
  Optional<Center> findByName(String name);

  /**
   * Verifica existencia por nombre.
   */
  boolean existsByName(String name);

  /**
   * Lista todos los Centers.
   */
  List<Center> findAll();

  /**
   * Elimina un Center por id.
   */
  void deleteById(UUID id);
}
