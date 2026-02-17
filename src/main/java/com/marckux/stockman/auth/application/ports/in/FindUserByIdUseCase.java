package com.marckux.stockman.auth.application.ports.in;

import java.util.UUID;

import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface FindUserByIdUseCase {

  UserResponse findUserById(UUID id);
}
