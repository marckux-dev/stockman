package com.marckux.stockman.auth.application.services.users;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marckux.stockman.auth.application.dtos.BlockUserCommand;
import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.BlockUserUseCase;
import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.model.Role;
import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockUser implements BlockUserUseCase {

  private final UserRepositoryPort userRepository;

  @Override
  @Transactional
  public UserResponse execute(BlockUserCommand command) {
    User requester = userRepository.findByEmail(Email.of(command.requesterEmail()).getValue())
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", command.requesterEmail()));

    User target = userRepository.findById(command.targetUserId())
      .orElseThrow(() -> new ResourceNotFoundException("Usuario", command.targetUserId().toString()));

    if (target.getRole() == Role.SUPER_ADMIN) {
      throw new InvalidAttributeException("Un SUPER_ADMIN no puede ser bloqueado");
    }

    if (requester.getRole() == Role.ADMIN) {
      if (target.getRole() != Role.USER) {
        throw new InvalidAttributeException("Un ADMIN solo puede bloquear usuarios USER");
      }
    } else if (requester.getRole() == Role.SUPER_ADMIN) {
      if (target.getRole() != Role.USER && target.getRole() != Role.ADMIN) {
        throw new InvalidAttributeException("Un SUPER_ADMIN solo puede bloquear USER o ADMIN");
      }
    } else {
      throw new InvalidAttributeException("No tienes permisos para bloquear usuarios");
    }

    User blocked = target.block();
    return UserResponse.fromDomain(userRepository.save(blocked));
  }
}
