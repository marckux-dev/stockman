package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Product;

public interface ProductRepositoryPort {

  Product save(Product product);

  Optional<Product> findById(UUID id);

  List<Product> findAll();

  void deleteById(UUID id);
}
