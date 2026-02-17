package com.marckux.stockman.auth.application.ports.in;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;

public interface LoginUseCase {

  LoginResponse login(LoginRequest request);
}
