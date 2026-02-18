package com.marckux.stockman.auth.application.services.users;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.application.dtos.UserResponse;
import com.marckux.stockman.auth.application.ports.in.usecases.FindAllUsersUseCase;
import com.marckux.stockman.auth.domain.ports.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindAllUsers implements FindAllUsersUseCase {

  private final UserRepositoryPort userRepository;

  @Override
  public List<UserResponse> execute(Void input) {
    return userRepository
      .findAll()
      .stream()
      .map(UserResponse::fromDomain)
      .collect(Collectors.toList())
    ;
  }
  
}
