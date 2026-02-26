package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Adapter de persistencia para Centers.
 */
@Component
@RequiredArgsConstructor
public class CenterPersistenceAdapter implements CenterRepositoryPort {

  private final JpaCenterRepository jpaCenterRepository;

  /**
   * Guarda un Center.
   */
  @Override
  public Center save(Center center) {
    CenterEntity entity = jpaCenterRepository.save(toEntity(center));
    return toDomain(entity);
  }

  /**
   * Busca un Center por id.
   */
  @Override
  public Optional<Center> findById(UUID id) {
    return jpaCenterRepository.findById(id)
      .filter(CenterEntity::isActive)
      .map(this::toDomain);
  }

  @Override
  public Optional<Center> findByIdIncludingInactive(UUID id) {
    return jpaCenterRepository.findById(id).map(this::toDomain);
  }

  /**
   * Busca un Center por nombre.
   */
  @Override
  public Optional<Center> findByName(String name) {
    return jpaCenterRepository.findByName(name)
      .filter(CenterEntity::isActive)
      .map(this::toDomain);
  }

  /**
   * Verifica existencia por nombre.
   */
  @Override
  public boolean existsByName(String name) {
    return jpaCenterRepository.findByName(name)
      .map(CenterEntity::isActive)
      .orElse(false);
  }

  /**
   * Lista todos los Centers.
   */
  @Override
  public List<Center> findAll() {
    return jpaCenterRepository.findAll()
      .stream()
      .filter(CenterEntity::isActive)
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  /**
   * Elimina un Center por id.
   */
  @Override
  public void deleteById(UUID id) {
    jpaCenterRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaCenterRepository.save(entity);
    });
  }

  /**
   * Convierte de entidad a dominio.
   */
  private Center toDomain(CenterEntity entity) {
    return Center.builder()
      .id(entity.getId())
      .name(entity.getName())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  /**
   * Convierte de dominio a entidad.
   */
  private CenterEntity toEntity(Center center) {
    return CenterEntity.builder()
      .id(center.getId())
      .name(center.getName())
      .isActive(center.isActive())
      .createdAt(center.getCreatedAt())
      .updatedAt(center.getUpdatedAt())
      .createdBy(center.getCreatedBy())
      .updatedBy(center.getUpdatedBy())
      .build();
  }
}
