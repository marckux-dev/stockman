package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;
import com.marckux.stockman.stock.domain.model.Category;
import com.marckux.stockman.stock.domain.model.Product;
import com.marckux.stockman.stock.domain.ports.out.CategoryRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

  private final JpaCategoryRepository jpaCategoryRepository;

  @Override
  public Category save(Category category) {
    CategoryEntity entity = jpaCategoryRepository.save(toEntity(category));
    return toDomain(entity);
  }

  @Override
  public Optional<Category> findById(UUID id) {
    return jpaCategoryRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<Category> findByName(String name) {
    return jpaCategoryRepository.findByName(name).map(this::toDomain);
  }

  @Override
  public boolean existsByName(String name) {
    return jpaCategoryRepository.existsByName(name);
  }

  @Override
  public List<Category> findAll() {
    return jpaCategoryRepository.findAll()
      .stream()
      .map(this::toDomain)
      .collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaCategoryRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setName(SoftDeleteNameHelper.buildRemovedName(entity.getName(), java.time.Instant.now()));
      jpaCategoryRepository.save(entity);
    });
  }

  private Category toDomain(CategoryEntity entity) {
    return Category.builder()
      .id(entity.getId())
      .name(entity.getName())
      .isActive(entity.isActive())
      .createdAt(entity.getCreatedAt())
      .updatedAt(entity.getUpdatedAt())
      .createdBy(entity.getCreatedBy())
      .updatedBy(entity.getUpdatedBy())
      .products(toDomainProducts(entity.getProducts()))
      .build();
  }

  private List<Product> toDomainProducts(List<ProductEntity> products) {
    if (products == null || products.isEmpty()) {
      return Collections.emptyList();
    }
    return products.stream()
      .map(this::toDomainProductShallow)
      .collect(Collectors.toList());
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

  private CategoryEntity toEntity(Category category) {
    return CategoryEntity.builder()
      .id(category.getId())
      .name(category.getName())
      .isActive(category.isActive())
      .createdAt(category.getCreatedAt())
      .updatedAt(category.getUpdatedAt())
      .createdBy(category.getCreatedBy())
      .updatedBy(category.getUpdatedBy())
      .build();
  }
}
