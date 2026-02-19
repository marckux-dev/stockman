package com.marckux.stockman.auth.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.marckux.stockman.auth.domain.model.User;

public interface UserRepositoryPort {
  
  User save(User user);
  Optional<User> findByEmail(String email);
  List<User> findAll();
  Optional<User> findById(UUID id);
  void deleteById(UUID id);
  
}
