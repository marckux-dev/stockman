package com.marckux.stockman.auth.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.marckux.stockman.auth.domain.model.ActivationStatus;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;
import com.marckux.stockman.shared.domain.utils.SoftDeleteNameHelper;

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
        .filter(UserEntity::isActive)
        .map(this::toDomain);
  }

  @Override
  public Optional<User> findByToken(String token) {
    return jpaUserRepository
      .findByToken(token)
      .filter(UserEntity::isActive)
      .map(this::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaUserRepository
        .findAll()
        .stream()
        .filter(UserEntity::isActive)
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaUserRepository
      .findById(id)
      .filter(UserEntity::isActive)
      .map(this::toDomain);  
  }

  @Override
  public void deleteById(UUID id) {
    jpaUserRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      entity.setEmail(SoftDeleteNameHelper.buildRemovedName(entity.getEmail(), java.time.Instant.now()));
      jpaUserRepository.save(entity);
    });
  }
  
  public JpaUserRepository getJpaUserRepository() {
    return jpaUserRepository;
  }

  private User toDomain(UserEntity userEntity) {
    HashedPassword hashedPassword = null;
    if (userEntity.getPassword() != null && !userEntity.getPassword().isBlank()) {
      hashedPassword = HashedPassword.of(userEntity.getPassword());
    }
    return User.builder()
        .id(userEntity.getId())
        .createdAt(userEntity.getCreatedAt())
        .updatedAt(userEntity.getUpdatedAt())
        .createdBy(userEntity.getCreatedBy())
        .updatedBy(userEntity.getUpdatedBy())
        .isActive(userEntity.isActive())
        .email(Email.of(userEntity.getEmail()))
        .hashedPassword(hashedPassword)
        .activationStatus(userEntity.getActivationStatus() != null
          ? userEntity.getActivationStatus()
          : ActivationStatus.INACTIVE)
        .token(userEntity.getToken())
        .tokenExpiration(userEntity.getTokenExpiration())
        .role(Role.valueOf(userEntity.getRole()))
        .build();
  }

  private UserEntity toEntity(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .email(user.getEmail().getValue())
        .password(user.getHashedPassword() != null ? user.getHashedPassword().getValue() : null)
        .activationStatus(user.getActivationStatus())
        .token(user.getToken())
        .tokenExpiration(user.getTokenExpiration())
        .role(user.getRole().name())
        .isActive(user.isActive())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .createdBy(user.getCreatedBy())
        .updatedBy(user.getUpdatedBy())
        .build();

  }

}
