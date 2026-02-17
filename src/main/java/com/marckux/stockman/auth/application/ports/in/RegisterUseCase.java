package com.marckux.stockman.auth.application.ports.in;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface RegisterUseCase {

  UserResponse register(RegisterRequest request);
}
