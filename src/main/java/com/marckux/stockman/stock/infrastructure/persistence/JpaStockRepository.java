package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockRepository extends JpaRepository<StockEntity, UUID> {

  Optional<StockEntity> findByProductIdAndLocationId(UUID productId, UUID locationId);
}
