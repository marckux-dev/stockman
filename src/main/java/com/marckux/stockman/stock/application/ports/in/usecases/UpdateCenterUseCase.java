package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.UpdateCenterRequest;

/**
 * Caso de uso: actualizar Center.
 */
public interface UpdateCenterUseCase extends UseCase<UpdateCenterUseCase.Input, CenterResponse> {

  /**
   * Input de actualización.
   */
  record Input(UUID id, UpdateCenterRequest request) {}
}
