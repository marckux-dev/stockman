package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.dtos.UpdateLocationRequest;

/**
 * Caso de uso: actualizar Location.
 */
public interface UpdateLocationUseCase extends UseCase<UpdateLocationUseCase.Input, LocationResponse> {

  /**
   * Input de actualización.
   */
  record Input(UUID id, UpdateLocationRequest request) {}
}
