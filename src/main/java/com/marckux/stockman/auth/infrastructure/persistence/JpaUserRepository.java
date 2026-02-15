package com.marckux.stockman.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID>{
  
  public Optional<UserEntity> findByEmail(String email);
  
}
