package com.marckux.stockman.stock.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.stock.domain.model.ActivePrinciple;

public interface ActivePrincipleRepositoryPort {

  ActivePrinciple save(ActivePrinciple activePrinciple);

  Optional<ActivePrinciple> findById(UUID id);

  List<ActivePrinciple> findAll();

  void deleteById(UUID id);
}
