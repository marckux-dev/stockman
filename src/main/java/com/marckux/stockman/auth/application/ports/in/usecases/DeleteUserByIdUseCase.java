package com.marckux.stockman.auth.application.ports.in.usecases;

import java.util.UUID;

public interface DeleteUserByIdUseCase extends UseCase<DeleteUserByIdUseCase.Input, Void> {

  record Input(UUID targetUserId, String requesterEmail) {}
}
