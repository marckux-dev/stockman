package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Expiration;

public interface ExpirationRepositoryPort {

  Expiration save(Expiration expiration);

  Optional<Expiration> findById(UUID id);

  List<Expiration> findAllByProductId(UUID productId);

  void deleteById(UUID id);
}
