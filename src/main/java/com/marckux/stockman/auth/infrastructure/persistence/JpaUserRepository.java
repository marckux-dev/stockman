package com.marckux.stockman.auth.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID>{

  public List<UserEntity> findAll();
  
  public Optional<UserEntity> findByEmail(String email);
  public Optional<UserEntity> findByToken(String token);

  public Optional<UserEntity> findById(UUID id);
  
}
