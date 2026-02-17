package com.marckux.stockman.auth.application.ports.in;

import java.util.List;

import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface FindAllUsersUseCase {

  List<UserResponse> findAll();
}
