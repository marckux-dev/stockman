package com.marckux.stockman.auth.application.services.users;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.PromoteUserToAdminUseCase;
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoteUserToAdmin implements PromoteUserToAdminUseCase {

  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public UserResponse execute(UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId.toString()));
    if (user.getRole() != Role.USER) {
      throw new InvalidAttributeException("Solo se puede promover de USER a ADMIN");
    }

    User updated = user.upgradeRole(Role.ADMIN);
    return UserResponse.fromDomain(userRepository.save(updated));
  }
}
