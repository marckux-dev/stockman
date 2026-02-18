package com.marckux.stockman.auth.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

  private final JpaUserRepository jpaUserRepository;

  @Override
  public User save(User user) {
    UserEntity userEntity = jpaUserRepository.save(toEntity(user));
    return toDomain(userEntity);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaUserRepository
        .findByEmail(email)
        .map(this::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaUserRepository
        .findAll()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaUserRepository
      .findById(id)
      .map(this::toDomain);  
  }
  
  public JpaUserRepository getJpaUserRepository() {
    return jpaUserRepository;
  }

  private User toDomain(UserEntity userEntity) {
    return User.builder()
        .id(userEntity.getId())
        .email(Email.of(userEntity.getEmail()))
        .hashedPassword(HashedPassword.of(userEntity.getPassword()))
        .name(userEntity.getName())
        .isActive(userEntity.isActive())
        .role(Role.valueOf(userEntity.getRole()))
        .build();
  }

  private UserEntity toEntity(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .email(user.getEmail().getValue())
        .password(user.getHashedPassword().getValue())
        .name(user.getName())
        .isActive(user.getIsActive())
        .role(user.getRole().name())
        .build();

  }


}
