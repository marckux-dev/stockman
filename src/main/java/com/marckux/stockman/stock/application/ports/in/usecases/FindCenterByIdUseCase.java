package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.CenterResponse;

/**
 * Caso de uso: buscar Center por id.
 */
public interface FindCenterByIdUseCase extends UseCase<UUID, CenterResponse> {
}
