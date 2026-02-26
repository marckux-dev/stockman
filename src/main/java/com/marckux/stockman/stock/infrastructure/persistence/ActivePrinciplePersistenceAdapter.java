package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Component;

import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;
import com.marckux.stockman.stock.domain.model.ActivePrinciple;
import com.marckux.stockman.stock.domain.model.Product;
import com.marckux.stockman.stock.domain.model.TherapeuticGroup;
import com.marckux.stockman.stock.domain.ports.out.ActivePrincipleRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivePrinciplePersistenceAdapter implements ActivePrincipleRepositoryPort {

  private final JpaActivePrincipleRepository jpaActivePrincipleRepository;
  private final EntityManager entityManager;

  @Override
  public ActivePrinciple save(ActivePrinciple activePrinciple) {
    ActivePrincipleEntity entity = jpaActivePrincipleRepository.save(toEntity(activePrinciple));
    return toDomain(entity);
  }

  @Override
  public Optional<ActivePrinciple> findById(UUID id) {
    return jpaActivePrincipleRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<ActivePrinciple> findAll() {
    return jpaActivePrincipleRepository.findAll()
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaActivePrincipleRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaActivePrincipleRepository.save(entity);
    });
  }

  private ActivePrinciple toDomain(ActivePrincipleEntity entity) {
    return ActivePrinciple.builder()
      .id(entity.getId())
      .name(entity.getName())
      .synonims(entity.getSynonims())
      .searchVector(entity.getSearchVector())
      .isActive(entity.isActive())
      .therapeuticGroup(toDomainTherapeuticGroupShallow(entity.getTherapeuticGroup()))
      .products(toDomainProducts(entity.getProducts()))
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private TherapeuticGroup toDomainTherapeuticGroupShallow(TherapeuticGroupEntity entity) {
    if (entity == null) {
      return null;
    }
    return TherapeuticGroup.builder()
      .id(entity.getId())
      .name(entity.getName())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private Set<Product> toDomainProducts(Set<ProductEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptySet();
    }
    return entities.stream()
      .map(this::toDomainProductShallow)
      .collect(Collectors.toSet());
  }

  private Product toDomainProductShallow(ProductEntity entity) {
    return Product.builder()
      .id(entity.getId())
      .name(entity.getName())
      .shortName(entity.getShortName())
      .synonims(entity.getSynonims())
      .searchVector(entity.getSearchVector())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private ActivePrincipleEntity toEntity(ActivePrinciple activePrinciple) {
    return ActivePrincipleEntity.builder()
      .id(activePrinciple.getId())
      .name(activePrinciple.getName())
      .synonims(activePrinciple.getSynonims())
      .searchVector(activePrinciple.getSearchVector())
      .isActive(activePrinciple.isActive())
      .therapeuticGroup(toEntityTherapeuticGroupRef(activePrinciple.getTherapeuticGroup()))
      .products(toEntityProductRefs(activePrinciple.getProducts()))
      .createdAt(activePrinciple.getCreatedAt())
      .updatedAt(activePrinciple.getUpdatedAt())
      .createdBy(activePrinciple.getCreatedBy())
      .updatedBy(activePrinciple.getUpdatedBy())
      .build();
  }

  private TherapeuticGroupEntity toEntityTherapeuticGroupRef(TherapeuticGroup group) {
    if (group == null || group.getId() == null) {
      return null;
    }
    return entityManager.getReference(TherapeuticGroupEntity.class, group.getId());
  }

  private Set<ProductEntity> toEntityProductRefs(Set<Product> products) {
    if (products == null || products.isEmpty()) {
      return Collections.emptySet();
    }
    return products.stream()
      .map(product -> entityManager.getReference(ProductEntity.class, product.getId()))
      .collect(Collectors.toSet());
  }

}
