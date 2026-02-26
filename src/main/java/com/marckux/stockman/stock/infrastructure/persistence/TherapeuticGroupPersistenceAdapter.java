package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;
import com.marckux.stockman.stock.domain.model.ActivePrinciple;
import com.marckux.stockman.stock.domain.model.TherapeuticGroup;
import com.marckux.stockman.stock.domain.ports.out.TherapeuticGroupRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TherapeuticGroupPersistenceAdapter implements TherapeuticGroupRepositoryPort {

  private final JpaTherapeuticGroupRepository jpaTherapeuticGroupRepository;

  @Override
  public TherapeuticGroup save(TherapeuticGroup group) {
    TherapeuticGroupEntity entity = jpaTherapeuticGroupRepository.save(toEntity(group));
    return toDomain(entity);
  }

  @Override
  public Optional<TherapeuticGroup> findById(UUID id) {
    return jpaTherapeuticGroupRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<TherapeuticGroup> findByName(String name) {
    return jpaTherapeuticGroupRepository.findByName(name).map(this::toDomain);
  }

  @Override
  public boolean existsByName(String name) {
    return jpaTherapeuticGroupRepository.existsByName(name);
  }

  @Override
  public List<TherapeuticGroup> findAll() {
    return jpaTherapeuticGroupRepository.findAll()
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaTherapeuticGroupRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaTherapeuticGroupRepository.save(entity);
    });
  }

  private TherapeuticGroup toDomain(TherapeuticGroupEntity entity) {
    return TherapeuticGroup.builder()
      .id(entity.getId())
      .name(entity.getName())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .activePrinciples(toDomainActivePrinciples(entity.getActivePrinciples()))
      .build();
  }

  private List<ActivePrinciple> toDomainActivePrinciples(List<ActivePrincipleEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream()
      .map(this::toDomainActivePrincipleShallow)
      .collect(Collectors.toList());
  }

  private ActivePrinciple toDomainActivePrincipleShallow(ActivePrincipleEntity entity) {
    return ActivePrinciple.builder()
      .id(entity.getId())
      .name(entity.getName())
      .synonims(entity.getSynonims())
      .searchVector(entity.getSearchVector())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private TherapeuticGroupEntity toEntity(TherapeuticGroup group) {
    return TherapeuticGroupEntity.builder()
      .id(group.getId())
      .name(group.getName())
      .isActive(group.isActive())
      .createdAt(group.getCreatedAt())
      .updatedAt(group.getUpdatedAt())
      .createdBy(group.getCreatedBy())
      .updatedBy(group.getUpdatedBy())
      .build();
  }
}
