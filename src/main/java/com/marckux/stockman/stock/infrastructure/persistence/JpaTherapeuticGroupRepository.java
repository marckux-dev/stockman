package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTherapeuticGroupRepository extends JpaRepository<TherapeuticGroupEntity, UUID> {

  Optional<TherapeuticGroupEntity> findByName(String name);

  boolean existsByName(String name);
}
