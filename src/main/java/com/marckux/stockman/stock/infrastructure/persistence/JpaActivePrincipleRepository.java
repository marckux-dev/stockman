package com.marckux.stockman.stock.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaActivePrincipleRepository extends JpaRepository<ActivePrincipleEntity, UUID> {
}
