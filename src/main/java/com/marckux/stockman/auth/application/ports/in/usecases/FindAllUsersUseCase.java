package com.marckux.stockman.auth.application.ports.in.usecases;

import java.util.List;

import com.marckux.stockman.auth.application.dtos.UserResponse;

public interface FindAllUsersUseCase extends UseCase<Void, List<UserResponse>> {
}
