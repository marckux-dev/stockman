package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.CreateLocationRequest;
import com.marckux.stockman.stock.application.dtos.LocationResponse;

/**
 * Caso de uso: crear Location.
 */
public interface CreateLocationUseCase extends UseCase<CreateLocationUseCase.Input, LocationResponse> {

  /**
   * Input de creación.
   */
  record Input(UUID centerId, CreateLocationRequest request) {}
}
