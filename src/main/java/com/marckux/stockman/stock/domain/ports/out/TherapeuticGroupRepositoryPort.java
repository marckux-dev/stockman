package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.TherapeuticGroup;

public interface TherapeuticGroupRepositoryPort {

  TherapeuticGroup save(TherapeuticGroup group);

  Optional<TherapeuticGroup> findById(UUID id);

  Optional<TherapeuticGroup> findByName(String name);

  boolean existsByName(String name);

  List<TherapeuticGroup> findAll();

  void deleteById(UUID id);
}
