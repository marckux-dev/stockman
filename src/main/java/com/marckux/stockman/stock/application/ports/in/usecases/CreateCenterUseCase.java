package com.marckux.stockman.stock.application.ports.in.usecases;

import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.dtos.CreateCenterRequest;

/**
 * Caso de uso: crear Center.
 */
public interface CreateCenterUseCase extends UseCase<CreateCenterUseCase.Input, CenterResponse> {

  /**
   * Input de creación.
   */
  record Input(CreateCenterRequest request) {}
}
