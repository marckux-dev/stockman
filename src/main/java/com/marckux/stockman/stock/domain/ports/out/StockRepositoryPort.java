package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Stock;

public interface StockRepositoryPort {

  Stock save(Stock stock);

  Optional<Stock> findById(UUID id);

  Optional<Stock> findByProductIdAndLocationId(UUID productId, UUID locationId);

  List<Stock> findAll();

  void deleteById(UUID id);
}
