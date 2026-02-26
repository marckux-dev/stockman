package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaExpirationRepository extends JpaRepository<ExpirationEntity, UUID> {

  List<ExpirationEntity> findAllByProductId(UUID productId);
}
