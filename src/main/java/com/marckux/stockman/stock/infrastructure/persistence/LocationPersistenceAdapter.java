package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Component;

import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;
import com.marckux.stockman.stock.domain.model.Center;
import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Adapter de persistencia para Locations.
 */
@Component
@RequiredArgsConstructor
public class LocationPersistenceAdapter implements LocationRepositoryPort {

  private final JpaLocationRepository jpaLocationRepository;
  private final EntityManager entityManager;

  /**
   * Guarda una Location.
   */
  @Override
  public Location save(Location location) {
    LocationEntity entity = jpaLocationRepository.save(toEntity(location));
    return toDomain(entity);
  }

  /**
   * Busca por id.
   */
  @Override
  public Optional<Location> findById(UUID id) {
    return jpaLocationRepository.findById(id)
      .filter(LocationEntity::isActive)
      .map(this::toDomain);
  }

  @Override
  public Optional<Location> findByIdIncludingInactive(UUID id) {
    return jpaLocationRepository.findById(id).map(this::toDomain);
  }

  /**
   * Busca por nombre y center.
   */
  @Override
  public Optional<Location> findByCenterIdAndName(UUID centerId, String name) {
    return jpaLocationRepository.findByCenterIdAndName(centerId, name)
      .filter(LocationEntity::isActive)
      .map(this::toDomain);
  }

  /**
   * Verifica existencia por nombre y center.
   */
  @Override
  public boolean existsByCenterIdAndName(UUID centerId, String name) {
    return jpaLocationRepository.existsByCenterIdAndName(centerId, name);
  }

  /**
   * Lista locations por center.
   */
  @Override
  public List<Location> findAllByCenterId(UUID centerId) {
    return jpaLocationRepository.findAllByCenterId(centerId)
      .stream()
      .filter(LocationEntity::isActive)
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  /**
   * Elimina por id.
   */
  @Override
  public void deleteById(UUID id) {
    jpaLocationRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaLocationRepository.save(entity);
    });
  }

  /**
   * Convierte de entidad a dominio.
   */
  private Location toDomain(LocationEntity entity) {
    return Location.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .center(toDomainCenter(entity.getCenter()))
      .parentLocation(toDomainParentLocation(entity.getParentLocation()))
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
  private LocationEntity toEntity(Location location) {
    return LocationEntity.builder()
      .id(location.getId())
      .name(location.getName())
      .description(location.getDescription())
      .center(entityManager.getReference(CenterEntity.class, location.getCenter().getId()))
      .parentLocation(toEntityParentLocationRef(location.getParentLocation()))
      .isActive(location.isActive())
      .createdAt(location.getCreatedAt())
      .updatedAt(location.getUpdatedAt())
      .createdBy(location.getCreatedBy())
      .updatedBy(location.getUpdatedBy())
      .build();
  }

  private Location toDomainParentLocation(LocationEntity entity) {
    if (entity == null) {
      return null;
    }
    return Location.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .center(toDomainCenter(entity.getCenter()))
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private LocationEntity toEntityParentLocationRef(Location parentLocation) {
    if (parentLocation == null || parentLocation.getId() == null) {
      return null;
    }
    return entityManager.getReference(LocationEntity.class, parentLocation.getId());
  }

  /**
   * Convierte CenterEntity a Center de dominio.
   */
  private Center toDomainCenter(CenterEntity entity) {
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
}
