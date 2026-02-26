package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

  Optional<CategoryEntity> findByName(String name);

  boolean existsByName(String name);
}
