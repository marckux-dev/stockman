package com.marckux.stockman.stock.application.services.centers;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.CenterResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.FindCenterByIdUseCase;
import com.marckux.stockman.stock.domain.ports.out.CenterRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para buscar Center por id.
 */
@Service
@RequiredArgsConstructor
public class FindCenterById implements FindCenterByIdUseCase {

  private final CenterRepositoryPort centerRepository;

  /**
   * Retorna un Center por id o lanza excepción si no existe.
   */
  @Override
  public CenterResponse execute(UUID id) {
    return centerRepository.findById(id)
      .map(CenterResponse::fromDomain)
      .orElseThrow(() -> new ResourceNotFoundException("Centro", id.toString()));
  }
}
