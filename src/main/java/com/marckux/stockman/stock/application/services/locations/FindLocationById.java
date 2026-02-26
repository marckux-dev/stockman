package com.marckux.stockman.stock.application.services.locations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.marckux.stockman.shared.domain.exceptions.ResourceNotFoundException;
import com.marckux.stockman.stock.application.dtos.LocationResponse;
import com.marckux.stockman.stock.application.ports.in.usecases.FindLocationByIdUseCase;
import com.marckux.stockman.stock.domain.ports.out.LocationRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de aplicación para buscar Location por id.
 */
@Service
@RequiredArgsConstructor
public class FindLocationById implements FindLocationByIdUseCase {

  private final LocationRepositoryPort locationRepository;

  /**
   * Retorna una Location por id.
   */
  @Override
  public LocationResponse execute(UUID id) {
    return locationRepository.findById(id)
      .map(LocationResponse::fromDomain)
      .orElseThrow(() -> new ResourceNotFoundException("Ubicación", id.toString()));
  }
}
