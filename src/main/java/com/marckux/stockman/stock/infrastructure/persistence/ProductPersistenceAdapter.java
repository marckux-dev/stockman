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
import com.marckux.stockman.stock.domain.model.Category;
import com.marckux.stockman.stock.domain.model.Expiration;
import com.marckux.stockman.stock.domain.model.Product;
import com.marckux.stockman.stock.domain.ports.out.ProductRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

  private final JpaProductRepository jpaProductRepository;
  private final EntityManager entityManager;

  @Override
  public Product save(Product product) {
    ProductEntity entity = jpaProductRepository.save(toEntity(product));
    return toDomain(entity);
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return jpaProductRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Product> findAll() {
    return jpaProductRepository.findAll()
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaProductRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaProductRepository.save(entity);
    });
  }

  private Product toDomain(ProductEntity entity) {
    return Product.builder()
      .id(entity.getId())
      .name(entity.getName())
      .shortName(entity.getShortName())
      .synonims(entity.getSynonims())
      .searchVector(entity.getSearchVector())
      .isActive(entity.isActive())
      .category(toDomainCategoryShallow(entity.getCategory()))
      .activePrinciples(toDomainActivePrinciples(entity.getActivePrinciples()))
      .expirations(toDomainExpirations(entity.getExpirations()))
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private Category toDomainCategoryShallow(CategoryEntity entity) {
    if (entity == null) {
      return null;
    }
    return Category.builder()
      .id(entity.getId())
      .name(entity.getName())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private Set<ActivePrinciple> toDomainActivePrinciples(Set<ActivePrincipleEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptySet();
    }
    return entities.stream()
      .map(this::toDomainActivePrincipleShallow)
      .collect(Collectors.toSet());
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

  private List<Expiration> toDomainExpirations(List<ExpirationEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream()
      .map(this::toDomainExpirationShallow)
      .collect(Collectors.toList());
  }

  private Expiration toDomainExpirationShallow(ExpirationEntity entity) {
    return Expiration.builder()
      .id(entity.getId())
      .date(entity.getDate())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private ProductEntity toEntity(Product product) {
    return ProductEntity.builder()
      .id(product.getId())
      .name(product.getName())
      .shortName(product.getShortName())
      .synonims(product.getSynonims())
      .searchVector(product.getSearchVector())
      .isActive(product.isActive())
      .category(toEntityCategoryRef(product.getCategory()))
      .activePrinciples(toEntityActivePrincipleRefs(product.getActivePrinciples()))
      .createdAt(product.getCreatedAt())
      .updatedAt(product.getUpdatedAt())
      .createdBy(product.getCreatedBy())
      .updatedBy(product.getUpdatedBy())
      .build();
  }

  private CategoryEntity toEntityCategoryRef(Category category) {
    if (category == null || category.getId() == null) {
      return null;
    }
    return entityManager.getReference(CategoryEntity.class, category.getId());
  }

  private Set<ActivePrincipleEntity> toEntityActivePrincipleRefs(Set<ActivePrinciple> activePrinciples) {
    if (activePrinciples == null || activePrinciples.isEmpty()) {
      return Collections.emptySet();
    }
    return activePrinciples.stream()
      .map(activePrinciple -> entityManager.getReference(ActivePrincipleEntity.class, activePrinciple.getId()))
      .collect(Collectors.toSet());
  }

}
