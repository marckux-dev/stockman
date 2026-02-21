package com.marckux.stockman.auth.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface PromoteUserToAdminUseCase extends UseCase<UUID, UserResponse> {
}
