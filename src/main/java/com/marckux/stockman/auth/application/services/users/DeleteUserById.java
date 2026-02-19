package com.marckux.stockman.auth.application.services.users;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.ports.in.usecases.DeleteUserByIdUseCase;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUserById implements DeleteUserByIdUseCase {

  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public Void execute(UUID id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", id.toString()));

    if (user.getRole() == Role.SUPER_ADMIN) {
      throw new InvalidAttributeException("No se puede eliminar un SUPER_ADMIN");
    }

    userRepository.deleteById(id);
    return null;
  }
}
