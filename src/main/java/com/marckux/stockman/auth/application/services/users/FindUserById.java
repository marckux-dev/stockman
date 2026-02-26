package com.marckux.stockman.auth.application.services.users;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.FindUserByIdUsecase;
import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindUserById implements FindUserByIdUsecase {

  private final UserRepositoryPort userRepository;

  @Override
  public UserResponse execute(UUID id) {
    return userRepository.findById(id)
      .map(UserResponse::fromDomain)
      .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
  }

  
}
