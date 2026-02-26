package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.List;
import java.util.UUID;

import com.marckux.stockman.stock.application.dtos.LocationResponse;

/**
 * Caso de uso: listar Locations por Center.
 */
public interface FindLocationsByCenterUseCase extends UseCase<UUID, List<LocationResponse>> {
}
