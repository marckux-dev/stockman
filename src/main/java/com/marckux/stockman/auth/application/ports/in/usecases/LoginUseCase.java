package com.marckux.stockman.auth.application.ports.in.usecases;

import com.marckux.stockman.auth.application.dtos.LoginRequest;
import com.marckux.stockman.auth.application.dtos.LoginResponse;

public interface LoginUseCase extends UseCase<LoginRequest, LoginResponse> {}
