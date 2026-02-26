package com.marckux.stockman.stock.application.ports.in.usecases;

import java.util.List;

import com.marckux.stockman.stock.application.dtos.CenterResponse;

/**
 * Caso de uso: listar Centers.
 */
public interface FindAllCentersUseCase extends UseCase<Void, List<CenterResponse>> {
}
