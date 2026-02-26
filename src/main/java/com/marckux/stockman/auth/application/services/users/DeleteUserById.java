package com.marckux.stockman.auth.application.services.users;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.ports.in.usecases.DeleteUserByIdUseCase;
import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUserById implements DeleteUserByIdUseCase {

  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public Void execute(Input input) {
    User requester = userRepository.findByEmail(Email.of(input.requesterEmail()).getValue())
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", input.requesterEmail()));

    User target = userRepository.findById(input.targetUserId())
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", input.targetUserId().toString()));

    if (target.getRole() == Role.SUPER_ADMIN) {
      throw new InvalidAttributeException("No se puede eliminar un SUPER_ADMIN");
    }

    if (requester.getRole() == Role.ADMIN && target.getRole() != Role.USER) {
      throw new InvalidAttributeException("Un ADMIN solo puede eliminar usuarios USER");
    }

    userRepository.deleteById(target.getId());
    return null;
  }
}
