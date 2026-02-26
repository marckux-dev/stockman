package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.LocationResponse;

/**
 * Caso de uso: buscar Location por id.
 */
public interface FindLocationByIdUseCase extends UseCase<UUID, LocationResponse> {
}
