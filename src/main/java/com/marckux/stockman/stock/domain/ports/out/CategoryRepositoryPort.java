package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.Category;

public interface CategoryRepositoryPort {

  Category save(Category category);

  Optional<Category> findById(UUID id);

  Optional<Category> findByName(String name);

  boolean existsByName(String name);

  List<Category> findAll();

  void deleteById(UUID id);
}
