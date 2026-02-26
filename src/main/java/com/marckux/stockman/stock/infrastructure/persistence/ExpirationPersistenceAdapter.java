package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Component;

import com.marckux.stockman.stock.domain.model.Expiration;
import com.marckux.stockman.stock.domain.model.Product;
import com.marckux.stockman.stock.domain.ports.out.ExpirationRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpirationPersistenceAdapter implements ExpirationRepositoryPort {

  private final JpaExpirationRepository jpaExpirationRepository;
  private final EntityManager entityManager;

  @Override
  public Expiration save(Expiration expiration) {
    ExpirationEntity entity = jpaExpirationRepository.save(toEntity(expiration));
    return toDomain(entity);
  }

  @Override
  public Optional<Expiration> findById(UUID id) {
    return jpaExpirationRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Expiration> findAllByProductId(UUID productId) {
    return jpaExpirationRepository.findAllByProductId(productId)
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaExpirationRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      jpaExpirationRepository.save(entity);
    });
  }

  private Expiration toDomain(ExpirationEntity entity) {
    return Expiration.builder()
      .id(entity.getId())
      .date(entity.getDate())
      .product(toDomainProductShallow(entity.getProduct()))
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private Product toDomainProductShallow(ProductEntity entity) {
    if (entity == null) {
      return null;
    }
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

  private ExpirationEntity toEntity(Expiration expiration) {
    return ExpirationEntity.builder()
      .id(expiration.getId())
      .date(expiration.getDate())
      .product(toEntityProductRef(expiration.getProduct()))
      .isActive(expiration.isActive())
      .createdAt(expiration.getCreatedAt())
      .updatedAt(expiration.getUpdatedAt())
      .createdBy(expiration.getCreatedBy())
      .updatedBy(expiration.getUpdatedBy())
      .build();
  }

  private ProductEntity toEntityProductRef(Product product) {
    if (product == null || product.getId() == null) {
      return null;
    }
    return entityManager.getReference(ProductEntity.class, product.getId());
  }

}
