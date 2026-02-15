package com.marckux.stockman.auth.application.ports;

import com.marckux.stockman.auth.application.dtos.AuthResponse;
import com.marckux.stockman.auth.application.dtos.LoginRequest;

public interface LoginUseCase {

  AuthResponse login(LoginRequest request);
}
