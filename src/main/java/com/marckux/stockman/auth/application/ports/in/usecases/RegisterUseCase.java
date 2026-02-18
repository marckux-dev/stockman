package com.marckux.stockman.auth.application.ports.in.usecases;

import com.marckux.stockman.auth.application.dtos.RegisterRequest;
import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface RegisterUseCase extends UseCase<RegisterRequest, UserResponse> {
}
