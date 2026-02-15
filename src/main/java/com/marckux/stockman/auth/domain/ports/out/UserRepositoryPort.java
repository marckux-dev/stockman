package com.marckux.stockman.auth.domain.ports.out;

import java.util.Optional;

import com.marckux.stockman.auth.domain.model.User;

public interface UserRepositoryPort {
  
  User save(User user);
  Optional<User> findByEmail(String email);
  
}
