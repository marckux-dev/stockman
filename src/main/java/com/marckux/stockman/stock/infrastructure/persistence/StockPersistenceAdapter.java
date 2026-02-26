package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Component;

import com.marckux.stockman.stock.domain.model.Location;
import com.marckux.stockman.stock.domain.model.Product;
import com.marckux.stockman.stock.domain.model.Stock;
import com.marckux.stockman.stock.domain.ports.out.StockRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockPersistenceAdapter implements StockRepositoryPort {

  private final JpaStockRepository jpaStockRepository;
  private final EntityManager entityManager;

  @Override
  public Stock save(Stock stock) {
    StockEntity entity = jpaStockRepository.save(toEntity(stock));
    return toDomain(entity);
  }

  @Override
  public Optional<Stock> findById(UUID id) {
    return jpaStockRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<Stock> findByProductIdAndLocationId(UUID productId, UUID locationId) {
    return jpaStockRepository.findByProductIdAndLocationId(productId, locationId)
      .map(this::toDomain);
  }

  @Override
  public List<Stock> findAll() {
    return jpaStockRepository.findAll()
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaStockRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      jpaStockRepository.save(entity);
    });
  }

  private Stock toDomain(StockEntity entity) {
    return Stock.builder()
      .id(entity.getId())
      .currentQuantity(entity.getCurrentQuantity())
      .idealQuantity(entity.getIdealQuantity())
      .threshold(entity.getThreshold())
      .isActive(entity.isActive())
      .product(toDomainProductShallow(entity.getProduct()))
      .location(toDomainLocationShallow(entity.getLocation()))
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

  private Location toDomainLocationShallow(LocationEntity entity) {
    if (entity == null) {
      return null;
    }
    return Location.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .build();
  }

  private StockEntity toEntity(Stock stock) {
    return StockEntity.builder()
      .id(stock.getId())
      .currentQuantity(stock.getCurrentQuantity())
      .idealQuantity(stock.getIdealQuantity())
      .threshold(stock.getThreshold())
      .isActive(stock.isActive())
      .product(toEntityProductRef(stock.getProduct()))
      .location(toEntityLocationRef(stock.getLocation()))
      .createdAt(stock.getCreatedAt())
      .updatedAt(stock.getUpdatedAt())
      .createdBy(stock.getCreatedBy())
      .updatedBy(stock.getUpdatedBy())
      .build();
  }

  private ProductEntity toEntityProductRef(Product product) {
    if (product == null || product.getId() == null) {
      return null;
    }
    return entityManager.getReference(ProductEntity.class, product.getId());
  }

  private LocationEntity toEntityLocationRef(Location location) {
    if (location == null || location.getId() == null) {
      return null;
    }
    return entityManager.getReference(LocationEntity.class, location.getId());
  }

}
