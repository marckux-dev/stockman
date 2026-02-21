package com.marckux.stockman.auth.application.ports.in.usecases;

import com.marckux.stockman.auth.application.dtos.BlockUserCommand;
import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface BlockUserUseCase extends UseCase<BlockUserCommand, UserResponse> {
}
